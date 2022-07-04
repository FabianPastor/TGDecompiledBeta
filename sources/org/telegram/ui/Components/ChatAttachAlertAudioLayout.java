package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatAttachAlertAudioLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        Context context2 = context;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        loadAudio();
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.frameLayout = frameLayout2;
        frameLayout2.setBackgroundColor(getThemedColor("dialogBackground"));
        AnonymousClass1 r0 = new SearchField(context2, false, resourcesProvider2) {
            public void onTextChange(String text) {
                if (text.length() == 0 && ChatAttachAlertAudioLayout.this.listView.getAdapter() != ChatAttachAlertAudioLayout.this.listAdapter) {
                    ChatAttachAlertAudioLayout.this.listView.setAdapter(ChatAttachAlertAudioLayout.this.listAdapter);
                    ChatAttachAlertAudioLayout.this.listAdapter.notifyDataSetChanged();
                }
                if (ChatAttachAlertAudioLayout.this.searchAdapter != null) {
                    ChatAttachAlertAudioLayout.this.searchAdapter.search(text);
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                ChatAttachAlertAudioLayout.this.parentAlert.makeFocusable(getSearchEditText(), true);
                return super.onInterceptTouchEvent(ev);
            }

            public void processTouchEvent(MotionEvent event) {
                MotionEvent e = MotionEvent.obtain(event);
                e.setLocation(e.getRawX(), (e.getRawY() - ChatAttachAlertAudioLayout.this.parentAlert.getSheetContainer().getTranslationY()) - ((float) AndroidUtilities.dp(58.0f)));
                ChatAttachAlertAudioLayout.this.listView.dispatchTouchEvent(e);
                e.recycle();
            }

            /* access modifiers changed from: protected */
            public void onFieldTouchUp(EditTextBoldCursor editText) {
                ChatAttachAlertAudioLayout.this.parentAlert.makeFocusable(editText, true);
            }
        };
        this.searchField = r0;
        r0.setHint(LocaleController.getString("SearchMusic", NUM));
        this.frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1, 51));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2, (View) null, resourcesProvider2);
        this.progressView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(ChatAttachAlertAudioLayout$$ExternalSyntheticLambda0.INSTANCE);
        ImageView imageView = new ImageView(context2);
        this.emptyImageView = imageView;
        imageView.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context2);
        this.emptyTitleTextView = textView;
        textView.setTextColor(getThemedColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context2);
        this.emptySubtitleTextView = textView2;
        textView2.setTextColor(getThemedColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        AnonymousClass2 r02 = new RecyclerListView(context2, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) ((ChatAttachAlertAudioLayout.this.parentAlert.scrollOffsetY[0] + AndroidUtilities.dp(30.0f)) + ((Build.VERSION.SDK_INT < 21 || ChatAttachAlertAudioLayout.this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)));
            }
        };
        this.listView = r02;
        r02.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass3 r03 = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(9.0f), this.listView) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        return super.calculateDyToMakeVisible(view, snapPreference) - (ChatAttachAlertAudioLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(7.0f));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
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
        this.listView.setGlowColor(getThemedColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatAttachAlertAudioLayout$$ExternalSyntheticLambda3(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new ChatAttachAlertAudioLayout$$ExternalSyntheticLambda4(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatAttachAlertAudioLayout.this.parentAlert.updateLayout(ChatAttachAlertAudioLayout.this, true, dy);
                ChatAttachAlertAudioLayout.this.updateEmptyViewPosition();
            }
        });
        this.searchAdapter = new SearchAdapter(context2);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context2);
        this.shadow = view;
        view.setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        addView(this.shadow, frameLayoutParams);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        updateEmptyView();
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertAudioLayout  reason: not valid java name */
    public /* synthetic */ void m754x64d1var_e(View view, int position) {
        onItemClick(view);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertAudioLayout  reason: not valid java name */
    public /* synthetic */ boolean m755x8e26456f(View view, int position) {
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
        View child;
        if (this.currentEmptyView.getVisibility() == 0 && (child = this.listView.getChildAt(0)) != null) {
            View view = this.currentEmptyView;
            view.setTranslationY(((float) (((view.getMeasuredHeight() - getMeasuredHeight()) + child.getTop()) / 2)) - (this.currentPanTranslationProgress / 2.0f));
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        boolean visible;
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
            visible = searchAdapter2.searchResult.isEmpty();
        } else {
            visible = this.audioEntries.isEmpty();
        }
        View view = this.currentEmptyView;
        if (visible) {
            i = 0;
        }
        view.setVisibility(i);
        updateEmptyViewPosition();
    }

    public void setMaxSelectedFiles(int value) {
        this.maxSelectedFiles = value;
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
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(true);
        } else {
            newOffset = top;
            runShadowAnimation(false);
        }
        this.frameLayout.setTranslationY((float) newOffset);
        return AndroidUtilities.dp(12.0f) + newOffset;
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(4.0f);
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
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
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateEmptyViewPosition();
    }

    /* access modifiers changed from: package-private */
    public void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = AndroidUtilities.dp(8.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (AndroidUtilities.isTablet() != 0 || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
                padding = (availableHeight / 5) * 2;
            } else {
                padding = (int) (((float) availableHeight) / 3.5f);
            }
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != padding) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(48.0f));
            this.ignoreLayout = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
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

    private void runShadowAnimation(final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
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
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ChatAttachAlertAudioLayout.this.shadowAnimation != null && ChatAttachAlertAudioLayout.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            ChatAttachAlertAudioLayout.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = ChatAttachAlertAudioLayout.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatAttachAlertAudioLayout.this.shadowAnimation != null && ChatAttachAlertAudioLayout.this.shadowAnimation.equals(animation)) {
                        AnimatorSet unused = ChatAttachAlertAudioLayout.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id != NotificationCenter.messagePlayingDidReset && id != NotificationCenter.messagePlayingDidStart && id != NotificationCenter.messagePlayingPlayStateChanged) {
            return;
        }
        if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.listView.getChildAt(a);
                if (view instanceof SharedAudioCell) {
                    SharedAudioCell cell = (SharedAudioCell) view;
                    if (cell.getMessage() != null) {
                        cell.updateButtonState(false, true);
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidStart && args[0].eventId == 0) {
            int count2 = this.listView.getChildCount();
            for (int a2 = 0; a2 < count2; a2++) {
                View view2 = this.listView.getChildAt(a2);
                if (view2 instanceof SharedAudioCell) {
                    SharedAudioCell cell2 = (SharedAudioCell) view2;
                    if (cell2.getMessage() != null) {
                        cell2.updateButtonState(false, true);
                    }
                }
            }
        }
    }

    private void showErrorBox(String error) {
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString("AppName", NUM)).setMessage(error).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).show();
    }

    private void onItemClick(View view) {
        boolean add;
        int i;
        if (view instanceof SharedAudioCell) {
            SharedAudioCell audioCell = (SharedAudioCell) view;
            MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) audioCell.getTag();
            int i2 = 1;
            if (this.selectedAudios.indexOfKey(audioEntry.id) >= 0) {
                this.selectedAudios.remove(audioEntry.id);
                this.selectedAudiosOrder.remove(audioEntry);
                audioCell.setChecked(false, true);
                add = false;
            } else if (this.maxSelectedFiles < 0 || this.selectedAudios.size() < (i = this.maxSelectedFiles)) {
                this.selectedAudios.put(audioEntry.id, audioEntry);
                this.selectedAudiosOrder.add(audioEntry);
                audioCell.setChecked(true, true);
                add = true;
            } else {
                showErrorBox(LocaleController.formatString("PassportUploadMaxReached", NUM, LocaleController.formatPluralString("Files", i, new Object[0])));
                return;
            }
            ChatAttachAlert chatAttachAlert = this.parentAlert;
            if (!add) {
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
    public void sendSelectedItems(boolean notify, int scheduleDate) {
        if (this.selectedAudios.size() != 0 && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList<MessageObject> audios = new ArrayList<>();
            for (int a = 0; a < this.selectedAudiosOrder.size(); a++) {
                audios.add(this.selectedAudiosOrder.get(a).messageObject);
            }
            this.delegate.didSelectAudio(audios, this.parentAlert.commentTextView.getText().toString(), notify, scheduleDate);
        }
    }

    public void setDelegate(AudioSelectDelegate audioSelectDelegate) {
        this.delegate = audioSelectDelegate;
    }

    private void loadAudio() {
        this.loadingAudio = true;
        Utilities.globalQueue.postRunnable(new ChatAttachAlertAudioLayout$$ExternalSyntheticLambda1(this));
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0188 A[SYNTHETIC, Splitter:B:30:0x0188] */
    /* renamed from: lambda$loadAudio$4$org-telegram-ui-Components-ChatAttachAlertAudioLayout  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m753x5var_a1() {
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
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0190 }
            android.content.ContentResolver r2 = r0.getContentResolver()     // Catch:{ Exception -> 0x0190 }
            android.net.Uri r3 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x0190 }
            java.lang.String r5 = "is_music != 0"
            r6 = 0
            java.lang.String r7 = "title"
            android.database.Cursor r0 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0190 }
            r2 = r0
            r0 = -2000000000(0xfffffffvar_ca6CLASSNAME, float:-1.2182823E-33)
        L_0x003e:
            boolean r3 = r2.moveToNext()     // Catch:{ all -> 0x0183 }
            if (r3 == 0) goto L_0x017c
            org.telegram.messenger.MediaController$AudioEntry r3 = new org.telegram.messenger.MediaController$AudioEntry     // Catch:{ all -> 0x0183 }
            r3.<init>()     // Catch:{ all -> 0x0183 }
            int r5 = r2.getInt(r8)     // Catch:{ all -> 0x0183 }
            long r5 = (long) r5     // Catch:{ all -> 0x0183 }
            r3.id = r5     // Catch:{ all -> 0x0183 }
            java.lang.String r5 = r2.getString(r9)     // Catch:{ all -> 0x0183 }
            r3.author = r5     // Catch:{ all -> 0x0183 }
            java.lang.String r5 = r2.getString(r10)     // Catch:{ all -> 0x0183 }
            r3.title = r5     // Catch:{ all -> 0x0183 }
            java.lang.String r5 = r2.getString(r11)     // Catch:{ all -> 0x0183 }
            r3.path = r5     // Catch:{ all -> 0x0183 }
            long r5 = r2.getLong(r12)     // Catch:{ all -> 0x0183 }
            r15 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r15
            int r6 = (int) r5     // Catch:{ all -> 0x0183 }
            r3.duration = r6     // Catch:{ all -> 0x0183 }
            java.lang.String r5 = r2.getString(r13)     // Catch:{ all -> 0x0183 }
            r3.genre = r5     // Catch:{ all -> 0x0183 }
            java.io.File r5 = new java.io.File     // Catch:{ all -> 0x0183 }
            java.lang.String r6 = r3.path     // Catch:{ all -> 0x0183 }
            r5.<init>(r6)     // Catch:{ all -> 0x0183 }
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x0183 }
            r6.<init>()     // Catch:{ all -> 0x0183 }
            r6.out = r9     // Catch:{ all -> 0x0183 }
            r6.id = r0     // Catch:{ all -> 0x0183 }
            org.telegram.tgnet.TLRPC$TL_peerUser r7 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0183 }
            r7.<init>()     // Catch:{ all -> 0x0183 }
            r6.peer_id = r7     // Catch:{ all -> 0x0183 }
            org.telegram.tgnet.TLRPC$TL_peerUser r7 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x0183 }
            r7.<init>()     // Catch:{ all -> 0x0183 }
            r6.from_id = r7     // Catch:{ all -> 0x0183 }
            org.telegram.tgnet.TLRPC$Peer r7 = r6.peer_id     // Catch:{ all -> 0x0183 }
            org.telegram.tgnet.TLRPC$Peer r10 = r6.from_id     // Catch:{ all -> 0x0183 }
            org.telegram.ui.Components.ChatAttachAlert r12 = r1.parentAlert     // Catch:{ all -> 0x0183 }
            int r12 = r12.currentAccount     // Catch:{ all -> 0x0183 }
            org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)     // Catch:{ all -> 0x0183 }
            r17 = r14
            long r13 = r12.getClientUserId()     // Catch:{ all -> 0x0177 }
            r10.user_id = r13     // Catch:{ all -> 0x0177 }
            r7.user_id = r13     // Catch:{ all -> 0x0177 }
            long r12 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0177 }
            long r12 = r12 / r15
            int r7 = (int) r12     // Catch:{ all -> 0x0177 }
            r6.date = r7     // Catch:{ all -> 0x0177 }
            java.lang.String r7 = ""
            r6.message = r7     // Catch:{ all -> 0x0177 }
            java.lang.String r7 = r3.path     // Catch:{ all -> 0x0177 }
            r6.attachPath = r7     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ all -> 0x0177 }
            r7.<init>()     // Catch:{ all -> 0x0177 }
            r6.media = r7     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r6.media     // Catch:{ all -> 0x0177 }
            int r10 = r7.flags     // Catch:{ all -> 0x0177 }
            r10 = r10 | r11
            r7.flags = r10     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$TL_document r10 = new org.telegram.tgnet.TLRPC$TL_document     // Catch:{ all -> 0x0177 }
            r10.<init>()     // Catch:{ all -> 0x0177 }
            r7.document = r10     // Catch:{ all -> 0x0177 }
            int r7 = r6.flags     // Catch:{ all -> 0x0177 }
            r7 = r7 | 768(0x300, float:1.076E-42)
            r6.flags = r7     // Catch:{ all -> 0x0177 }
            java.lang.String r7 = org.telegram.messenger.FileLoader.getFileExtension(r5)     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0177 }
            r12 = 0
            r10.id = r12     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0177 }
            r10.access_hash = r12     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0177 }
            byte[] r12 = new byte[r8]     // Catch:{ all -> 0x0177 }
            r10.file_reference = r12     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0177 }
            int r12 = r6.date     // Catch:{ all -> 0x0177 }
            r10.date = r12     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0177 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0177 }
            r12.<init>()     // Catch:{ all -> 0x0177 }
            java.lang.String r13 = "audio/"
            r12.append(r13)     // Catch:{ all -> 0x0177 }
            int r13 = r7.length()     // Catch:{ all -> 0x0177 }
            if (r13 <= 0) goto L_0x010b
            r13 = r7
            goto L_0x010d
        L_0x010b:
            java.lang.String r13 = "mp3"
        L_0x010d:
            r12.append(r13)     // Catch:{ all -> 0x0177 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x0177 }
            r10.mime_type = r12     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0177 }
            long r12 = r5.length()     // Catch:{ all -> 0x0177 }
            int r13 = (int) r12     // Catch:{ all -> 0x0177 }
            long r12 = (long) r13     // Catch:{ all -> 0x0177 }
            r10.size = r12     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$Document r10 = r10.document     // Catch:{ all -> 0x0177 }
            r10.dc_id = r8     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r10 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ all -> 0x0177 }
            r10.<init>()     // Catch:{ all -> 0x0177 }
            int r12 = r3.duration     // Catch:{ all -> 0x0177 }
            r10.duration = r12     // Catch:{ all -> 0x0177 }
            java.lang.String r12 = r3.title     // Catch:{ all -> 0x0177 }
            r10.title = r12     // Catch:{ all -> 0x0177 }
            java.lang.String r12 = r3.author     // Catch:{ all -> 0x0177 }
            r10.performer = r12     // Catch:{ all -> 0x0177 }
            int r12 = r10.flags     // Catch:{ all -> 0x0177 }
            r12 = r12 | r11
            r10.flags = r12     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$Document r12 = r12.document     // Catch:{ all -> 0x0177 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r12.attributes     // Catch:{ all -> 0x0177 }
            r12.add(r10)     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r12 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename     // Catch:{ all -> 0x0177 }
            r12.<init>()     // Catch:{ all -> 0x0177 }
            java.lang.String r13 = r5.getName()     // Catch:{ all -> 0x0177 }
            r12.file_name = r13     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$MessageMedia r13 = r6.media     // Catch:{ all -> 0x0177 }
            org.telegram.tgnet.TLRPC$Document r13 = r13.document     // Catch:{ all -> 0x0177 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r13.attributes     // Catch:{ all -> 0x0177 }
            r13.add(r12)     // Catch:{ all -> 0x0177 }
            org.telegram.messenger.MessageObject r13 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x0177 }
            org.telegram.ui.Components.ChatAttachAlert r14 = r1.parentAlert     // Catch:{ all -> 0x0177 }
            int r14 = r14.currentAccount     // Catch:{ all -> 0x0177 }
            r13.<init>(r14, r6, r8, r9)     // Catch:{ all -> 0x0177 }
            r3.messageObject = r13     // Catch:{ all -> 0x0177 }
            r13 = r17
            r13.add(r3)     // Catch:{ all -> 0x0174 }
            int r0 = r0 + -1
            r14 = r13
            r10 = 2
            r12 = 4
            r13 = 5
            goto L_0x003e
        L_0x0174:
            r0 = move-exception
            r3 = r0
            goto L_0x0186
        L_0x0177:
            r0 = move-exception
            r13 = r17
            r3 = r0
            goto L_0x0186
        L_0x017c:
            r13 = r14
            if (r2 == 0) goto L_0x0182
            r2.close()     // Catch:{ Exception -> 0x018e }
        L_0x0182:
            goto L_0x0195
        L_0x0183:
            r0 = move-exception
            r13 = r14
            r3 = r0
        L_0x0186:
            if (r2 == 0) goto L_0x018d
            r2.close()     // Catch:{ all -> 0x018c }
            goto L_0x018d
        L_0x018c:
            r0 = move-exception
        L_0x018d:
            throw r3     // Catch:{ Exception -> 0x018e }
        L_0x018e:
            r0 = move-exception
            goto L_0x0192
        L_0x0190:
            r0 = move-exception
            r13 = r14
        L_0x0192:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0195:
            org.telegram.ui.Components.ChatAttachAlertAudioLayout$$ExternalSyntheticLambda2 r0 = new org.telegram.ui.Components.ChatAttachAlertAudioLayout$$ExternalSyntheticLambda2
            r0.<init>(r1, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertAudioLayout.m753x5var_a1():void");
    }

    /* renamed from: lambda$loadAudio$3$org-telegram-ui-Components-ChatAttachAlertAudioLayout  reason: not valid java name */
    public /* synthetic */ void m752x361e2160(ArrayList newAudioEntries) {
        this.loadingAudio = false;
        this.audioEntries = newAudioEntries;
        this.listAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ChatAttachAlertAudioLayout.this.audioEntries.size() + 1 + (ChatAttachAlertAudioLayout.this.audioEntries.isEmpty() ^ true ? 1 : 0);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            AnonymousClass1 view;
            switch (viewType) {
                case 0:
                    AnonymousClass1 r0 = new SharedAudioCell(this.mContext, ChatAttachAlertAudioLayout.this.resourcesProvider) {
                        public boolean needPlayMessage(MessageObject messageObject) {
                            MessageObject unused = ChatAttachAlertAudioLayout.this.playingAudio = messageObject;
                            ArrayList<MessageObject> arrayList = new ArrayList<>();
                            arrayList.add(messageObject);
                            return MediaController.getInstance().setPlaylist(arrayList, messageObject, 0);
                        }
                    };
                    r0.setCheckForButtonPress(true);
                    AnonymousClass1 r1 = r0;
                    view = r0;
                    break;
                case 1:
                    View view2 = new View(this.mContext);
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    view = view2;
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                int position2 = position - 1;
                MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) ChatAttachAlertAudioLayout.this.audioEntries.get(position2);
                SharedAudioCell audioCell = (SharedAudioCell) holder.itemView;
                audioCell.setTag(audioEntry);
                boolean z = true;
                audioCell.setMessageObject(audioEntry.messageObject, position2 != ChatAttachAlertAudioLayout.this.audioEntries.size() - 1);
                if (ChatAttachAlertAudioLayout.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                    z = false;
                }
                audioCell.setChecked(z, false);
            }
        }

        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 2;
            }
            if (i == 0) {
                return 1;
            }
            return 0;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertAudioLayout.this.updateEmptyView();
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastReqId;
        private int lastSearchId;
        private Context mContext;
        private int reqId = 0;
        /* access modifiers changed from: private */
        public ArrayList<MediaController.AudioEntry> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(String query) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                if (!this.searchResult.isEmpty()) {
                    this.searchResult.clear();
                }
                if (ChatAttachAlertAudioLayout.this.listView.getAdapter() != ChatAttachAlertAudioLayout.this.listAdapter) {
                    ChatAttachAlertAudioLayout.this.listView.setAdapter(ChatAttachAlertAudioLayout.this.listAdapter);
                }
                notifyDataSetChanged();
                return;
            }
            int searchId = this.lastSearchId + 1;
            this.lastSearchId = searchId;
            ChatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda1 chatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda1 = new ChatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda1(this, query, searchId);
            this.searchRunnable = chatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda1;
            AndroidUtilities.runOnUIThread(chatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda1, 300);
        }

        /* renamed from: lambda$search$1$org-telegram-ui-Components-ChatAttachAlertAudioLayout$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m757xb4e282d3(String query, int searchId) {
            Utilities.searchQueue.postRunnable(new ChatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda2(this, query, new ArrayList<>(ChatAttachAlertAudioLayout.this.audioEntries), searchId));
        }

        /* renamed from: lambda$search$0$org-telegram-ui-Components-ChatAttachAlertAudioLayout$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m756xCLASSNAMEvar_(String query, ArrayList copy, int searchId) {
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList(), query, this.lastSearchId);
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<MediaController.AudioEntry> resultArray = new ArrayList<>();
            for (int a = 0; a < copy.size(); a++) {
                MediaController.AudioEntry entry = (MediaController.AudioEntry) copy.get(a);
                int b = 0;
                while (true) {
                    if (b >= search.length) {
                        break;
                    }
                    String q = search[b];
                    boolean ok = false;
                    if (entry.author != null) {
                        ok = entry.author.toLowerCase().contains(q);
                    }
                    if (!ok && entry.title != null) {
                        ok = entry.title.toLowerCase().contains(q);
                    }
                    if (ok) {
                        resultArray.add(entry);
                        break;
                    }
                    b++;
                }
            }
            updateSearchResults(resultArray, query, searchId);
        }

        private void updateSearchResults(ArrayList<MediaController.AudioEntry> result, String query, int searchId) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda0(this, searchId, query, result));
        }

        /* renamed from: lambda$updateSearchResults$2$org-telegram-ui-Components-ChatAttachAlertAudioLayout$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m758x2081177(int searchId, String query, ArrayList result) {
            if (searchId == this.lastSearchId) {
                if (!(searchId == -1 || ChatAttachAlertAudioLayout.this.listView.getAdapter() == ChatAttachAlertAudioLayout.this.searchAdapter)) {
                    ChatAttachAlertAudioLayout.this.listView.setAdapter(ChatAttachAlertAudioLayout.this.searchAdapter);
                }
                if (ChatAttachAlertAudioLayout.this.listView.getAdapter() == ChatAttachAlertAudioLayout.this.searchAdapter) {
                    ChatAttachAlertAudioLayout.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoAudioFoundInfo", NUM, query)));
                }
                this.searchResult = result;
                notifyDataSetChanged();
            }
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertAudioLayout.this.updateEmptyView();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return this.searchResult.size() + 1 + (this.searchResult.isEmpty() ^ true ? 1 : 0);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            AnonymousClass1 view;
            switch (viewType) {
                case 0:
                    AnonymousClass1 r0 = new SharedAudioCell(this.mContext, ChatAttachAlertAudioLayout.this.resourcesProvider) {
                        public boolean needPlayMessage(MessageObject messageObject) {
                            MessageObject unused = ChatAttachAlertAudioLayout.this.playingAudio = messageObject;
                            ArrayList<MessageObject> arrayList = new ArrayList<>();
                            arrayList.add(messageObject);
                            return MediaController.getInstance().setPlaylist(arrayList, messageObject, 0);
                        }
                    };
                    r0.setCheckForButtonPress(true);
                    AnonymousClass1 r1 = r0;
                    view = r0;
                    break;
                case 1:
                    View view2 = new View(this.mContext);
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    view = view2;
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                int position2 = position - 1;
                MediaController.AudioEntry audioEntry = this.searchResult.get(position2);
                SharedAudioCell audioCell = (SharedAudioCell) holder.itemView;
                audioCell.setTag(audioEntry);
                boolean z = true;
                audioCell.setMessageObject(audioEntry.messageObject, position2 != this.searchResult.size() - 1);
                if (ChatAttachAlertAudioLayout.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                    z = false;
                }
                audioCell.setChecked(z, false);
            }
        }

        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 2;
            }
            if (i == 0) {
                return 1;
            }
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void onContainerTranslationUpdated(float currentPanTranslationY) {
        this.currentPanTranslationProgress = currentPanTranslationY;
        super.onContainerTranslationUpdated(currentPanTranslationY);
        updateEmptyViewPosition();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.frameLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchBackground(), ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"searchIconImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"clearSearchImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchText"));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchHint"));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        themeDescriptions.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        themeDescriptions.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        themeDescriptions.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        themeDescriptions.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        return themeDescriptions;
    }
}
