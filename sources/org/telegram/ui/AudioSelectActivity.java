package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AudioEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class AudioSelectActivity extends BaseFragment implements NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private ArrayList<AudioEntry> audioEntries = new ArrayList();
    private ChatActivity chatActivity;
    protected EditTextEmoji commentTextView;
    private AudioSelectActivityDelegate delegate;
    private ImageView emptyImageView;
    private TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    protected FrameLayout frameLayout2;
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingAudio;
    private Paint paint = new Paint(1);
    private MessageObject playingAudio;
    private EmptyTextProgressView progressView;
    private RectF rect = new RectF();
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private LongSparseArray<AudioEntry> selectedAudios = new LongSparseArray();
    protected View selectedCountView;
    private ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    protected View shadow;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private TextPaint textPaint = new TextPaint(1);
    private ImageView writeButton;
    protected FrameLayout writeButtonContainer;

    public interface AudioSelectActivityDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList, CharSequence charSequence, boolean z, int i);
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

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
            return AudioSelectActivity.this.audioEntries.size();
        }

        public Object getItem(int i) {
            return AudioSelectActivity.this.audioEntries.get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AnonymousClass1 anonymousClass1 = new SharedAudioCell(this.mContext) {
                public boolean needPlayMessage(MessageObject messageObject) {
                    AudioSelectActivity.this.playingAudio = messageObject;
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(messageObject);
                    return MediaController.getInstance().setPlaylist(arrayList, messageObject);
                }
            };
            anonymousClass1.setCheckForButtonPress(true);
            return new Holder(anonymousClass1);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            AudioEntry audioEntry = (AudioEntry) AudioSelectActivity.this.audioEntries.get(i);
            SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
            sharedAudioCell.setTag(audioEntry);
            boolean z = true;
            sharedAudioCell.setMessageObject(audioEntry.messageObject, i != AudioSelectActivity.this.audioEntries.size() - 1);
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                z = false;
            }
            sharedAudioCell.setChecked(z, false);
        }
    }

    public class SearchAdapter extends SelectionAdapter {
        private int lastReqId;
        private Context mContext;
        private int reqId = 0;
        private ArrayList<AudioEntry> searchResult = new ArrayList();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

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
                if (AudioSelectActivity.this.listView.getAdapter() != AudioSelectActivity.this.listViewAdapter) {
                    AudioSelectActivity.this.listView.setAdapter(AudioSelectActivity.this.listViewAdapter);
                }
                notifyDataSetChanged();
                return;
            }
            -$$Lambda$AudioSelectActivity$SearchAdapter$oB5vontvK6nB1LUEbL62PoV4_BY -__lambda_audioselectactivity_searchadapter_ob5vontvk6nb1luebl62pov4_by = new -$$Lambda$AudioSelectActivity$SearchAdapter$oB5vontvK6nB1LUEbL62PoV4_BY(this, str);
            this.searchRunnable = -__lambda_audioselectactivity_searchadapter_ob5vontvk6nb1luebl62pov4_by;
            AndroidUtilities.runOnUIThread(-__lambda_audioselectactivity_searchadapter_ob5vontvk6nb1luebl62pov4_by, 300);
        }

        public /* synthetic */ void lambda$search$1$AudioSelectActivity$SearchAdapter(String str) {
            Utilities.searchQueue.postRunnable(new -$$Lambda$AudioSelectActivity$SearchAdapter$NkdztFGuT0my4DvvTdwmxCykdLk(this, str, new ArrayList(AudioSelectActivity.this.audioEntries)));
        }

        public /* synthetic */ void lambda$null$0$AudioSelectActivity$SearchAdapter(String str, ArrayList arrayList) {
            String toLowerCase = str.trim().toLowerCase();
            if (toLowerCase.length() == 0) {
                updateSearchResults(new ArrayList(), str);
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
            if (toLowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
            strArr[0] = toLowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                AudioEntry audioEntry = (AudioEntry) arrayList.get(i);
                for (CharSequence charSequence : strArr) {
                    String str2 = audioEntry.author;
                    boolean contains = str2 != null ? str2.toLowerCase().contains(charSequence) : false;
                    if (!contains) {
                        String str3 = audioEntry.title;
                        if (str3 != null) {
                            contains = str3.toLowerCase().contains(charSequence);
                        }
                    }
                    if (contains) {
                        arrayList2.add(audioEntry);
                        break;
                    }
                }
            }
            updateSearchResults(arrayList2, str);
        }

        private void updateSearchResults(ArrayList<AudioEntry> arrayList, String str) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$AudioSelectActivity$SearchAdapter$luYzBSYmi69uB0Perlcwy7EOqTM(this, str, arrayList));
        }

        public /* synthetic */ void lambda$updateSearchResults$2$AudioSelectActivity$SearchAdapter(String str, ArrayList arrayList) {
            if (AudioSelectActivity.this.searching) {
                if (AudioSelectActivity.this.listView.getAdapter() != AudioSelectActivity.this.searchAdapter) {
                    AudioSelectActivity.this.listView.setAdapter(AudioSelectActivity.this.searchAdapter);
                    AudioSelectActivity.this.updateEmptyView();
                }
                AudioSelectActivity.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoAudioFoundInfo", NUM, str)));
            }
            AudioSelectActivity.this.searchWas = true;
            this.searchResult = arrayList;
            notifyDataSetChanged();
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public AudioEntry getItem(int i) {
            return (AudioEntry) this.searchResult.get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AnonymousClass1 anonymousClass1 = new SharedAudioCell(this.mContext) {
                public boolean needPlayMessage(MessageObject messageObject) {
                    AudioSelectActivity.this.playingAudio = messageObject;
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(messageObject);
                    return MediaController.getInstance().setPlaylist(arrayList, messageObject);
                }
            };
            anonymousClass1.setCheckForButtonPress(true);
            return new Holder(anonymousClass1);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            AudioEntry audioEntry = (AudioEntry) this.searchResult.get(i);
            SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
            sharedAudioCell.setTag(audioEntry);
            boolean z = true;
            sharedAudioCell.setMessageObject(audioEntry.messageObject, i != this.searchResult.size() - 1);
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                z = false;
            }
            sharedAudioCell.setChecked(z, false);
        }
    }

    public AudioSelectActivity(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        loadAudio();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        if (this.playingAudio != null && MediaController.getInstance().isPlayingMessage(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.searching = false;
        String str = "dialogBackground";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        String str2 = "dialogTextBlack";
        this.actionBar.setTitleColor(Theme.getColor(str2));
        this.actionBar.setItemsColor(Theme.getColor(str2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    AudioSelectActivity.this.finishFragment();
                }
            }
        });
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                AudioSelectActivity.this.searching = true;
            }

            public void onSearchCollapse() {
                AudioSelectActivity.this.searching = false;
                if (AudioSelectActivity.this.listView.getAdapter() != AudioSelectActivity.this.listViewAdapter) {
                    AudioSelectActivity.this.listView.setAdapter(AudioSelectActivity.this.listViewAdapter);
                }
                AudioSelectActivity.this.updateEmptyView();
                AudioSelectActivity.this.searchAdapter.search(null);
            }

            public void onTextChanged(EditText editText) {
                AudioSelectActivity.this.searchAdapter.search(editText.getText().toString());
            }
        });
        String str3 = "Search";
        this.searchItem.setSearchFieldHint(LocaleController.getString(str3, NUM));
        this.searchItem.setContentDescription(LocaleController.getString(str3, NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor(str2));
        searchField.setCursorColor(Theme.getColor(str2));
        searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        this.sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastItemSize;
            private int lastNotifyWidth;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int i3 = 0;
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    EditTextEmoji editTextEmoji = AudioSelectActivity.this.commentTextView;
                    if (editTextEmoji != null) {
                        this.ignoreLayout = true;
                        editTextEmoji.hideEmojiView();
                        this.ignoreLayout = false;
                    }
                } else if (!AndroidUtilities.isInMultiwindow) {
                    AudioSelectActivity audioSelectActivity = AudioSelectActivity.this;
                    if (audioSelectActivity.commentTextView != null && audioSelectActivity.frameLayout2.getParent() == this) {
                        size2 -= AudioSelectActivity.this.commentTextView.getEmojiPadding();
                        i2 = MeasureSpec.makeMeasureSpec(size2, NUM);
                    }
                }
                int childCount = getChildCount();
                while (i3 < childCount) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        EditTextEmoji editTextEmoji2 = AudioSelectActivity.this.commentTextView;
                        if (editTextEmoji2 == null || !editTextEmoji2.isPopupView(childAt)) {
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
            /* JADX WARNING: Removed duplicated region for block: B:52:0x00ed  */
            /* JADX WARNING: Removed duplicated region for block: B:51:0x00e4  */
            /* JADX WARNING: Removed duplicated region for block: B:43:0x00c5  */
            /* JADX WARNING: Removed duplicated region for block: B:36:0x00ab  */
            /* JADX WARNING: Removed duplicated region for block: B:51:0x00e4  */
            /* JADX WARNING: Removed duplicated region for block: B:52:0x00ed  */
            public void onLayout(boolean r9, int r10, int r11, int r12, int r13) {
                /*
                r8 = this;
                r9 = r8.lastNotifyWidth;
                r12 = r12 - r10;
                if (r9 == r12) goto L_0x0024;
            L_0x0005:
                r8.lastNotifyWidth = r12;
                r9 = org.telegram.ui.AudioSelectActivity.this;
                r9 = r9.sendPopupWindow;
                if (r9 == 0) goto L_0x0024;
            L_0x000f:
                r9 = org.telegram.ui.AudioSelectActivity.this;
                r9 = r9.sendPopupWindow;
                r9 = r9.isShowing();
                if (r9 == 0) goto L_0x0024;
            L_0x001b:
                r9 = org.telegram.ui.AudioSelectActivity.this;
                r9 = r9.sendPopupWindow;
                r9.dismiss();
            L_0x0024:
                r9 = r8.getChildCount();
                r10 = org.telegram.ui.AudioSelectActivity.this;
                r0 = r10.commentTextView;
                r1 = 0;
                if (r0 == 0) goto L_0x0056;
            L_0x002f:
                r10 = r10.frameLayout2;
                r10 = r10.getParent();
                if (r10 != r8) goto L_0x0056;
            L_0x0037:
                r10 = r8.getKeyboardHeight();
                r0 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
                if (r10 > r0) goto L_0x0056;
            L_0x0043:
                r10 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                if (r10 != 0) goto L_0x0056;
            L_0x0047:
                r10 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r10 != 0) goto L_0x0056;
            L_0x004d:
                r10 = org.telegram.ui.AudioSelectActivity.this;
                r10 = r10.commentTextView;
                r10 = r10.getEmojiPadding();
                goto L_0x0057;
            L_0x0056:
                r10 = 0;
            L_0x0057:
                r8.setBottomClip(r10);
            L_0x005a:
                if (r1 >= r9) goto L_0x0104;
            L_0x005c:
                r0 = r8.getChildAt(r1);
                r2 = r0.getVisibility();
                r3 = 8;
                if (r2 != r3) goto L_0x006a;
            L_0x0068:
                goto L_0x0100;
            L_0x006a:
                r2 = r0.getLayoutParams();
                r2 = (android.widget.FrameLayout.LayoutParams) r2;
                r3 = r0.getMeasuredWidth();
                r4 = r0.getMeasuredHeight();
                r5 = r2.gravity;
                r6 = -1;
                if (r5 != r6) goto L_0x007f;
            L_0x007d:
                r5 = 51;
            L_0x007f:
                r6 = r5 & 7;
                r5 = r5 & 112;
                r6 = r6 & 7;
                r7 = 1;
                if (r6 == r7) goto L_0x009d;
            L_0x0088:
                r7 = 5;
                if (r6 == r7) goto L_0x0093;
            L_0x008b:
                r6 = r2.leftMargin;
                r7 = r8.getPaddingLeft();
                r6 = r6 + r7;
                goto L_0x00a7;
            L_0x0093:
                r6 = r12 - r3;
                r7 = r2.rightMargin;
                r6 = r6 - r7;
                r7 = r8.getPaddingRight();
                goto L_0x00a6;
            L_0x009d:
                r6 = r12 - r3;
                r6 = r6 / 2;
                r7 = r2.leftMargin;
                r6 = r6 + r7;
                r7 = r2.rightMargin;
            L_0x00a6:
                r6 = r6 - r7;
            L_0x00a7:
                r7 = 16;
                if (r5 == r7) goto L_0x00c5;
            L_0x00ab:
                r7 = 48;
                if (r5 == r7) goto L_0x00bd;
            L_0x00af:
                r7 = 80;
                if (r5 == r7) goto L_0x00b6;
            L_0x00b3:
                r2 = r2.topMargin;
                goto L_0x00d2;
            L_0x00b6:
                r5 = r13 - r10;
                r5 = r5 - r11;
                r5 = r5 - r4;
                r2 = r2.bottomMargin;
                goto L_0x00d0;
            L_0x00bd:
                r2 = r2.topMargin;
                r5 = r8.getPaddingTop();
                r2 = r2 + r5;
                goto L_0x00d2;
            L_0x00c5:
                r5 = r13 - r10;
                r5 = r5 - r11;
                r5 = r5 - r4;
                r5 = r5 / 2;
                r7 = r2.topMargin;
                r5 = r5 + r7;
                r2 = r2.bottomMargin;
            L_0x00d0:
                r2 = r5 - r2;
            L_0x00d2:
                r5 = org.telegram.ui.AudioSelectActivity.this;
                r5 = r5.commentTextView;
                if (r5 == 0) goto L_0x00fb;
            L_0x00d8:
                r5 = r5.isPopupView(r0);
                if (r5 == 0) goto L_0x00fb;
            L_0x00de:
                r2 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r2 == 0) goto L_0x00ed;
            L_0x00e4:
                r2 = r8.getMeasuredHeight();
                r5 = r0.getMeasuredHeight();
                goto L_0x00fa;
            L_0x00ed:
                r2 = r8.getMeasuredHeight();
                r5 = r8.getKeyboardHeight();
                r2 = r2 + r5;
                r5 = r0.getMeasuredHeight();
            L_0x00fa:
                r2 = r2 - r5;
            L_0x00fb:
                r3 = r3 + r6;
                r4 = r4 + r2;
                r0.layout(r6, r2, r3, r4);
            L_0x0100:
                r1 = r1 + 1;
                goto L_0x005a;
            L_0x0104:
                r8.notifyHeightChanged();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AudioSelectActivity$AnonymousClass3.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor(str));
        this.fragmentView = this.sizeNotifierFrameLayout;
        this.progressView = new EmptyTextProgressView(context2);
        this.progressView.showProgress();
        this.sizeNotifierFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView = new LinearLayout(context2);
        this.emptyView.setOrientation(1);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(-$$Lambda$AudioSelectActivity$TsXMP3OMn_MrWzh_GvC6AvDPzsY.INSTANCE);
        this.emptyImageView = new ImageView(context2);
        this.emptyImageView.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        this.emptyTitleTextView = new TextView(context2);
        String str4 = "dialogEmptyText";
        this.emptyTitleTextView.setTextColor(Theme.getColor(str4));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        this.emptySubtitleTextView = new TextView(context2);
        this.emptySubtitleTextView.setTextColor(Theme.getColor(str4));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        this.listView = new RecyclerListView(context2);
        this.listView.setClipToPadding(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(50.0f));
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.searchAdapter = new SearchAdapter(context2);
        this.listView.setOnItemClickListener(new -$$Lambda$AudioSelectActivity$udgf5SVc7GntWL9Gfvar_UulPGZU(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$AudioSelectActivity$FMrqNgxsH6ltBQCOi57F5VhZEn0(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && AudioSelectActivity.this.searching && AudioSelectActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(AudioSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.shadow = new View(context2);
        this.shadow.setBackgroundResource(NUM);
        this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.frameLayout2 = new FrameLayout(context2);
        this.frameLayout2.setBackgroundColor(Theme.getColor(str));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener(-$$Lambda$AudioSelectActivity$70c_2QVPUHQ3blJpxsqysqHDZJ0.INSTANCE);
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, null, 1);
        this.commentTextView.setFilters(new InputFilter[]{new LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
        this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
        this.commentTextView.onResume();
        searchField = this.commentTextView.getEditText();
        searchField.setMaxLines(1);
        searchField.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        this.writeButtonContainer = new FrameLayout(context2);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", NUM));
        this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        this.writeButtonContainer.setOnClickListener(new -$$Lambda$AudioSelectActivity$yCkpzfYdMW7ExlFmoo7A5yK5n7o(this));
        this.writeButtonContainer.setOnLongClickListener(new -$$Lambda$AudioSelectActivity$op1868hUHQ4ktj1ACa-ggMteGuo(this));
        this.writeButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("dialogFloatingButton"), Theme.getColor("dialogFloatingButtonPressed"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
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
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, AudioSelectActivity.this.selectedAudios.size()))});
                int ceil = (int) Math.ceil((double) AudioSelectActivity.this.textPaint.measureText(format));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                AudioSelectActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                AudioSelectActivity.this.paint.setColor(Theme.getColor("dialogBackground"));
                max /= 2;
                int i = measuredWidth - max;
                max += measuredWidth;
                AudioSelectActivity.this.rect.set((float) i, 0.0f, (float) max, (float) getMeasuredHeight());
                canvas.drawRoundRect(AudioSelectActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), AudioSelectActivity.this.paint);
                AudioSelectActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                AudioSelectActivity.this.rect.set((float) (i + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (max - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(AudioSelectActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), AudioSelectActivity.this.paint);
                canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), AudioSelectActivity.this.textPaint);
            }
        };
        this.selectedCountView.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
        updateEmptyView();
        updateCountButton(0);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$AudioSelectActivity(View view, int i) {
        onItemClick(view);
    }

    public /* synthetic */ boolean lambda$createView$2$AudioSelectActivity(View view, int i) {
        onItemClick(view);
        return true;
    }

    public /* synthetic */ void lambda$createView$4$AudioSelectActivity(View view) {
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity == null || !chatActivity.isInScheduleMode()) {
            sendSelectedAudios(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.chatActivity.getCurrentUser()), new -$$Lambda$AudioSelectActivity$hHkzuGuuIaYgsTAzjw1szVPzJSU(this));
        }
    }

    public /* synthetic */ boolean lambda$createView$7$AudioSelectActivity(View view) {
        View view2 = view;
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity == null) {
            return false;
        }
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
                    if (motionEvent.getActionMasked() == 0 && AudioSelectActivity.this.sendPopupWindow != null && AudioSelectActivity.this.sendPopupWindow.isShowing()) {
                        view.getHitRect(this.popupRect);
                        if (!this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            AudioSelectActivity.this.sendPopupWindow.dismiss();
                        }
                    }
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new -$$Lambda$AudioSelectActivity$oS8Sb2J5Tspbv-4MUmOh4HRW3hk(this));
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
                    this.itemCells[i].setOnClickListener(new -$$Lambda$AudioSelectActivity$vLs-KPE_ctH6hQ4c9WSG4z1LicY(this, i));
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
        return false;
    }

    public /* synthetic */ void lambda$null$5$AudioSelectActivity(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$null$6$AudioSelectActivity(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.chatActivity.getCurrentUser()), new -$$Lambda$AudioSelectActivity$hHkzuGuuIaYgsTAzjw1szVPzJSU(this));
        } else if (i == 1) {
            sendSelectedAudios(true, 0);
        }
    }

    private void updateEmptyView() {
        if (this.loadingAudio) {
            this.listView.setEmptyView(this.progressView);
            this.emptyView.setVisibility(8);
            return;
        }
        if (this.searching) {
            this.emptyTitleTextView.setText(LocaleController.getString("NoAudioFound", NUM));
            this.emptyView.setGravity(1);
            this.emptyView.setPadding(0, AndroidUtilities.dp(60.0f), 0, 0);
            this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        } else {
            this.emptyTitleTextView.setText(LocaleController.getString("NoAudioFiles", NUM));
            this.emptySubtitleTextView.setText(LocaleController.getString("NoAudioFilesInfo", NUM));
            this.emptyView.setGravity(17);
            this.emptyView.setPadding(0, 0, 0, 0);
            this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        }
        this.listView.setEmptyView(this.emptyView);
        this.progressView.setVisibility(8);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged) {
            View childAt;
            SharedAudioCell sharedAudioCell;
            if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
                i = this.listView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    childAt = this.listView.getChildAt(i2);
                    if (childAt instanceof SharedAudioCell) {
                        sharedAudioCell = (SharedAudioCell) childAt;
                        if (sharedAudioCell.getMessage() != null) {
                            sharedAudioCell.updateButtonState(false, true);
                        }
                    }
                }
            } else if (i == NotificationCenter.messagePlayingDidStart && ((MessageObject) objArr[0]).eventId == 0) {
                i = this.listView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    childAt = this.listView.getChildAt(i2);
                    if (childAt instanceof SharedAudioCell) {
                        sharedAudioCell = (SharedAudioCell) childAt;
                        if (sharedAudioCell.getMessage() != null) {
                            sharedAudioCell.updateButtonState(false, true);
                        }
                    }
                }
            }
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return super.onBackPressed();
        }
        this.commentTextView.hidePopup(true);
        return false;
    }

    private void onItemClick(View view) {
        SharedAudioCell sharedAudioCell = (SharedAudioCell) view;
        AudioEntry audioEntry = (AudioEntry) sharedAudioCell.getTag();
        boolean z = false;
        int i = 1;
        if (this.selectedAudios.indexOfKey(audioEntry.id) >= 0) {
            this.selectedAudios.remove(audioEntry.id);
            sharedAudioCell.setChecked(false, true);
        } else {
            this.selectedAudios.put(audioEntry.id, audioEntry);
            sharedAudioCell.setChecked(true, true);
            z = true;
        }
        if (!z) {
            i = 2;
        }
        updateCountButton(i);
    }

    private boolean showCommentTextView(final boolean z, boolean z2) {
        if (this.commentTextView == null) {
            return false;
        }
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(z ? Integer.valueOf(1) : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        float f = 0.0f;
        float f2 = 0.2f;
        float f3 = 1.0f;
        if (z2) {
            this.animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            FrameLayout frameLayout = this.writeButtonContainer;
            Property property = View.SCALE_X;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            frameLayout = this.writeButtonContainer;
            property = View.SCALE_Y;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            frameLayout = this.writeButtonContainer;
            property = View.ALPHA;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            View view = this.selectedCountView;
            property = View.SCALE_X;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
            view = this.selectedCountView;
            property = View.SCALE_Y;
            fArr = new float[1];
            if (z) {
                f2 = 1.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
            View view2 = this.selectedCountView;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!z) {
                f3 = 0.0f;
            }
            fArr2[0] = f3;
            arrayList.add(ObjectAnimator.ofFloat(view2, property2, fArr2));
            FrameLayout frameLayout2 = this.frameLayout2;
            Property property3 = View.TRANSLATION_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 0.0f : (float) AndroidUtilities.dp(48.0f);
            arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property3, fArr3));
            view2 = this.shadow;
            property3 = View.TRANSLATION_Y;
            fArr3 = new float[1];
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            fArr3[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view2, property3, fArr3));
            this.animatorSet.playTogether(arrayList);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(AudioSelectActivity.this.animatorSet)) {
                        if (!z) {
                            AudioSelectActivity.this.frameLayout2.setVisibility(4);
                            AudioSelectActivity.this.writeButtonContainer.setVisibility(4);
                        }
                        AudioSelectActivity.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(AudioSelectActivity.this.animatorSet)) {
                        AudioSelectActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
            View view3 = this.selectedCountView;
            if (z) {
                f2 = 1.0f;
            }
            view3.setScaleY(f2);
            view3 = this.selectedCountView;
            if (!z) {
                f3 = 0.0f;
            }
            view3.setAlpha(f3);
            this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
            view3 = this.shadow;
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            view3.setTranslationY(f);
            if (!z) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    public void updateCountButton(int i) {
        boolean z = true;
        if (this.selectedAudios.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            if (i == 0) {
                z = false;
            }
            showCommentTextView(false, z);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true, i != 0) || i == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = i == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        view = this.selectedCountView;
        property = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (i != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view, property, fArr2);
        animatorSet.playTogether(animatorArr);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(180);
        animatorSet.start();
    }

    private void sendSelectedAudios(boolean z, int i) {
        if (this.selectedAudios.size() != 0 && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.selectedAudios.size(); i2++) {
                arrayList.add(((AudioEntry) this.selectedAudios.valueAt(i2)).messageObject);
            }
            this.delegate.didSelectAudio(arrayList, this.commentTextView.getText().toString(), z, i);
            finishFragment();
        }
    }

    public void setDelegate(AudioSelectActivityDelegate audioSelectActivityDelegate) {
        this.delegate = audioSelectActivityDelegate;
    }

    private void loadAudio() {
        this.loadingAudio = true;
        Utilities.globalQueue.postRunnable(new -$$Lambda$AudioSelectActivity$0AW56Hl2qVFIVsXD4PDb-caMNoU(this));
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:24:0x016a */
    /* JADX WARNING: Missing block: B:19:0x0163, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:20:0x0164, code skipped:
            r3 = r0;
     */
    /* JADX WARNING: Missing block: B:21:0x0165, code skipped:
            if (r2 != null) goto L_0x0167;
     */
    /* JADX WARNING: Missing block: B:23:?, code skipped:
            r2.close();
     */
    public /* synthetic */ void lambda$loadAudio$9$AudioSelectActivity() {
        /*
        r16 = this;
        r1 = r16;
        r0 = 6;
        r4 = new java.lang.String[r0];
        r0 = 0;
        r2 = "_id";
        r4[r0] = r2;
        r8 = 1;
        r2 = "artist";
        r4[r8] = r2;
        r9 = 2;
        r2 = "title";
        r4[r9] = r2;
        r10 = 3;
        r2 = "_data";
        r4[r10] = r2;
        r11 = 4;
        r2 = "duration";
        r4[r11] = r2;
        r12 = 5;
        r2 = "album";
        r4[r12] = r2;
        r13 = new java.util.ArrayList;
        r13.<init>();
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x016b }
        r2 = r2.getContentResolver();	 Catch:{ Exception -> 0x016b }
        r3 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x016b }
        r5 = "is_music != 0";
        r6 = 0;
        r7 = "title";
        r2 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x016b }
        r3 = -NUM; // 0xfffffffvar_ca6CLASSNAME float:-1.2182823E-33 double:NaN;
    L_0x003c:
        r4 = r2.moveToNext();	 Catch:{ all -> 0x0161 }
        if (r4 == 0) goto L_0x015b;
    L_0x0042:
        r4 = new org.telegram.messenger.MediaController$AudioEntry;	 Catch:{ all -> 0x0161 }
        r4.<init>();	 Catch:{ all -> 0x0161 }
        r5 = r2.getInt(r0);	 Catch:{ all -> 0x0161 }
        r5 = (long) r5;	 Catch:{ all -> 0x0161 }
        r4.id = r5;	 Catch:{ all -> 0x0161 }
        r5 = r2.getString(r8);	 Catch:{ all -> 0x0161 }
        r4.author = r5;	 Catch:{ all -> 0x0161 }
        r5 = r2.getString(r9);	 Catch:{ all -> 0x0161 }
        r4.title = r5;	 Catch:{ all -> 0x0161 }
        r5 = r2.getString(r10);	 Catch:{ all -> 0x0161 }
        r4.path = r5;	 Catch:{ all -> 0x0161 }
        r5 = r2.getLong(r11);	 Catch:{ all -> 0x0161 }
        r14 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 / r14;
        r6 = (int) r5;	 Catch:{ all -> 0x0161 }
        r4.duration = r6;	 Catch:{ all -> 0x0161 }
        r5 = r2.getString(r12);	 Catch:{ all -> 0x0161 }
        r4.genre = r5;	 Catch:{ all -> 0x0161 }
        r5 = new java.io.File;	 Catch:{ all -> 0x0161 }
        r6 = r4.path;	 Catch:{ all -> 0x0161 }
        r5.<init>(r6);	 Catch:{ all -> 0x0161 }
        r6 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ all -> 0x0161 }
        r6.<init>();	 Catch:{ all -> 0x0161 }
        r6.out = r8;	 Catch:{ all -> 0x0161 }
        r6.id = r3;	 Catch:{ all -> 0x0161 }
        r7 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ all -> 0x0161 }
        r7.<init>();	 Catch:{ all -> 0x0161 }
        r6.to_id = r7;	 Catch:{ all -> 0x0161 }
        r7 = r6.to_id;	 Catch:{ all -> 0x0161 }
        r8 = r1.currentAccount;	 Catch:{ all -> 0x0161 }
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);	 Catch:{ all -> 0x0161 }
        r8 = r8.getClientUserId();	 Catch:{ all -> 0x0161 }
        r6.from_id = r8;	 Catch:{ all -> 0x0161 }
        r7.user_id = r8;	 Catch:{ all -> 0x0161 }
        r7 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0161 }
        r7 = r7 / r14;
        r8 = (int) r7;	 Catch:{ all -> 0x0161 }
        r6.date = r8;	 Catch:{ all -> 0x0161 }
        r7 = "";
        r6.message = r7;	 Catch:{ all -> 0x0161 }
        r7 = r4.path;	 Catch:{ all -> 0x0161 }
        r6.attachPath = r7;	 Catch:{ all -> 0x0161 }
        r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ all -> 0x0161 }
        r7.<init>();	 Catch:{ all -> 0x0161 }
        r6.media = r7;	 Catch:{ all -> 0x0161 }
        r7 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r7.flags;	 Catch:{ all -> 0x0161 }
        r8 = r8 | r10;
        r7.flags = r8;	 Catch:{ all -> 0x0161 }
        r7 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = new org.telegram.tgnet.TLRPC$TL_document;	 Catch:{ all -> 0x0161 }
        r8.<init>();	 Catch:{ all -> 0x0161 }
        r7.document = r8;	 Catch:{ all -> 0x0161 }
        r7 = r6.flags;	 Catch:{ all -> 0x0161 }
        r7 = r7 | 768;
        r6.flags = r7;	 Catch:{ all -> 0x0161 }
        r7 = org.telegram.messenger.FileLoader.getFileExtension(r5);	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r14 = 0;
        r8.id = r14;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r8.access_hash = r14;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r14 = new byte[r0];	 Catch:{ all -> 0x0161 }
        r8.file_reference = r14;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r14 = r6.date;	 Catch:{ all -> 0x0161 }
        r8.date = r14;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r14 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0161 }
        r14.<init>();	 Catch:{ all -> 0x0161 }
        r15 = "audio/";
        r14.append(r15);	 Catch:{ all -> 0x0161 }
        r15 = r7.length();	 Catch:{ all -> 0x0161 }
        if (r15 <= 0) goto L_0x00fb;
    L_0x00fa:
        goto L_0x00fd;
    L_0x00fb:
        r7 = "mp3";
    L_0x00fd:
        r14.append(r7);	 Catch:{ all -> 0x0161 }
        r7 = r14.toString();	 Catch:{ all -> 0x0161 }
        r8.mime_type = r7;	 Catch:{ all -> 0x0161 }
        r7 = r6.media;	 Catch:{ all -> 0x0161 }
        r7 = r7.document;	 Catch:{ all -> 0x0161 }
        r14 = r5.length();	 Catch:{ all -> 0x0161 }
        r8 = (int) r14;	 Catch:{ all -> 0x0161 }
        r7.size = r8;	 Catch:{ all -> 0x0161 }
        r7 = r6.media;	 Catch:{ all -> 0x0161 }
        r7 = r7.document;	 Catch:{ all -> 0x0161 }
        r7.dc_id = r0;	 Catch:{ all -> 0x0161 }
        r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;	 Catch:{ all -> 0x0161 }
        r7.<init>();	 Catch:{ all -> 0x0161 }
        r8 = r4.duration;	 Catch:{ all -> 0x0161 }
        r7.duration = r8;	 Catch:{ all -> 0x0161 }
        r8 = r4.title;	 Catch:{ all -> 0x0161 }
        r7.title = r8;	 Catch:{ all -> 0x0161 }
        r8 = r4.author;	 Catch:{ all -> 0x0161 }
        r7.performer = r8;	 Catch:{ all -> 0x0161 }
        r8 = r7.flags;	 Catch:{ all -> 0x0161 }
        r8 = r8 | r10;
        r7.flags = r8;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r8 = r8.attributes;	 Catch:{ all -> 0x0161 }
        r8.add(r7);	 Catch:{ all -> 0x0161 }
        r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;	 Catch:{ all -> 0x0161 }
        r7.<init>();	 Catch:{ all -> 0x0161 }
        r5 = r5.getName();	 Catch:{ all -> 0x0161 }
        r7.file_name = r5;	 Catch:{ all -> 0x0161 }
        r5 = r6.media;	 Catch:{ all -> 0x0161 }
        r5 = r5.document;	 Catch:{ all -> 0x0161 }
        r5 = r5.attributes;	 Catch:{ all -> 0x0161 }
        r5.add(r7);	 Catch:{ all -> 0x0161 }
        r5 = new org.telegram.messenger.MessageObject;	 Catch:{ all -> 0x0161 }
        r7 = r1.currentAccount;	 Catch:{ all -> 0x0161 }
        r5.<init>(r7, r6, r0);	 Catch:{ all -> 0x0161 }
        r4.messageObject = r5;	 Catch:{ all -> 0x0161 }
        r13.add(r4);	 Catch:{ all -> 0x0161 }
        r3 = r3 + -1;
        r8 = 1;
        goto L_0x003c;
    L_0x015b:
        if (r2 == 0) goto L_0x016f;
    L_0x015d:
        r2.close();	 Catch:{ Exception -> 0x016b }
        goto L_0x016f;
    L_0x0161:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x0163 }
    L_0x0163:
        r0 = move-exception;
        r3 = r0;
        if (r2 == 0) goto L_0x016a;
    L_0x0167:
        r2.close();	 Catch:{ all -> 0x016a }
    L_0x016a:
        throw r3;	 Catch:{ Exception -> 0x016b }
    L_0x016b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x016f:
        r0 = new org.telegram.ui.-$$Lambda$AudioSelectActivity$qHu3jZT6Csp1zx-1DQDOfjLHr4c;
        r0.<init>(r1, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AudioSelectActivity.lambda$loadAudio$9$AudioSelectActivity():void");
    }

    public /* synthetic */ void lambda$null$8$AudioSelectActivity(ArrayList arrayList) {
        this.loadingAudio = false;
        this.audioEntries = arrayList;
        if (this.audioEntries.isEmpty()) {
            this.searchItem.setVisibility(8);
        }
        updateEmptyView();
        this.listViewAdapter.notifyDataSetChanged();
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[23];
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r1[14] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        r1[15] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        View view = this.listView;
        int i = ThemeDescription.FLAG_CHECKBOX;
        Class[] clsArr = new Class[]{SharedAudioCell.class};
        String[] strArr = new String[1];
        strArr[0] = "checkBox";
        r1[16] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "checkbox");
        r1[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck");
        r1[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, "windowBackgroundWhiteBlackText");
        r1[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, "windowBackgroundWhiteGrayText2");
        r1[20] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "dialogFloatingIcon");
        r1[21] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "dialogFloatingButton");
        r1[22] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "dialogFloatingButtonPressed");
        return r1;
    }
}
