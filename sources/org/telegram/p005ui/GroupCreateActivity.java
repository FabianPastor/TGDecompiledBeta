package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BackDrawable;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Adapters.SearchAdapterHelper;
import org.telegram.p005ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate$$CC;
import org.telegram.p005ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.p005ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.p005ui.Cells.GroupCreateSectionCell;
import org.telegram.p005ui.Cells.GroupCreateUserCell;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.p005ui.Components.GroupCreateSpan;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.FastScrollAdapter;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.GroupCreateActivity */
public class GroupCreateActivity extends BaseFragment implements OnClickListener, NotificationCenterDelegate {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
    private int chatId;
    private int chatType = 0;
    private int containerHeight;
    private GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivityDelegate delegate;
    private View doneButton;
    private boolean doneButtonVisible;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private ImageView floatingButton;
    private boolean ignoreScrollEvent;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private boolean isNeverShare;
    private GroupCreateDividerItemDecoration itemDecoration;
    private RecyclerListView listView;
    private int maxCount = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private SparseArray<GroupCreateSpan> selectedContacts = new SparseArray();
    private boolean showFabButton;
    private SpansContainer spansContainer;

    /* renamed from: org.telegram.ui.GroupCreateActivity$10 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            GroupCreateActivity.this.floatingButton.setVisibility(4);
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$5 */
    class CLASSNAME implements Callback {
        CLASSNAME() {
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$6 */
    class CLASSNAME implements OnKeyListener {
        private boolean wasEmpty;

        CLASSNAME() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == 67) {
                if (event.getAction() == 0) {
                    this.wasEmpty = GroupCreateActivity.this.editText.length() == 0;
                } else if (event.getAction() == 1 && this.wasEmpty && !GroupCreateActivity.this.allSpans.isEmpty()) {
                    GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
                    GroupCreateActivity.this.updateHint();
                    GroupCreateActivity.this.checkVisibleRows();
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$7 */
    class CLASSNAME implements TextWatcher {
        CLASSNAME() {
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
            if (GroupCreateActivity.this.editText.length() != 0) {
                GroupCreateActivity.this.searching = true;
                GroupCreateActivity.this.searchWas = true;
                GroupCreateActivity.this.adapter.setSearching(true);
                GroupCreateActivity.this.itemDecoration.setSearching(true);
                GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
                GroupCreateActivity.this.listView.setFastScrollVisible(false);
                GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                GroupCreateActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                return;
            }
            GroupCreateActivity.this.closeSearch();
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$9 */
    class CLASSNAME extends ViewOutlineProvider {
        CLASSNAME() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$GroupCreateActivityDelegate */
    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Integer> arrayList);
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$SpansContainer */
    private class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList();
        private AnimatorSet currentAnimation;
        private View removingSpan;

        /* renamed from: org.telegram.ui.GroupCreateActivity$SpansContainer$1 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

            public void onAnimationEnd(Animator animator) {
                SpansContainer.this.addingSpan = null;
                SpansContainer.this.currentAnimation = null;
                SpansContainer.this.animationStarted = false;
                GroupCreateActivity.this.editText.setAllowDrawCursor(true);
            }
        }

        public SpansContainer(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int minWidth;
            int count = getChildCount();
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - AndroidUtilities.m9dp(32.0f);
            int currentLineWidth = 0;
            int y = AndroidUtilities.m9dp(12.0f);
            int allCurrentLineWidth = 0;
            int allY = AndroidUtilities.m9dp(12.0f);
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(32.0f), NUM));
                    if (child != this.removingSpan && child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                        y += child.getMeasuredHeight() + AndroidUtilities.m9dp(12.0f);
                        currentLineWidth = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.m9dp(12.0f);
                        allCurrentLineWidth = 0;
                    }
                    int x = AndroidUtilities.m9dp(16.0f) + currentLineWidth;
                    if (!this.animationStarted) {
                        if (child == this.removingSpan) {
                            child.setTranslationX((float) (AndroidUtilities.m9dp(16.0f) + allCurrentLineWidth));
                            child.setTranslationY((float) allY);
                        } else if (this.removingSpan != null) {
                            if (child.getTranslationX() != ((float) x)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationX", new float[]{(float) x}));
                            }
                            if (child.getTranslationY() != ((float) y)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationY", new float[]{(float) y}));
                            }
                        } else {
                            child.setTranslationX((float) x);
                            child.setTranslationY((float) y);
                        }
                    }
                    if (child != this.removingSpan) {
                        currentLineWidth += child.getMeasuredWidth() + AndroidUtilities.m9dp(9.0f);
                    }
                    allCurrentLineWidth += child.getMeasuredWidth() + AndroidUtilities.m9dp(9.0f);
                }
            }
            if (AndroidUtilities.isTablet()) {
                minWidth = AndroidUtilities.m9dp(366.0f) / 3;
            } else {
                minWidth = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.m9dp(164.0f)) / 3;
            }
            if (maxWidth - currentLineWidth < minWidth) {
                currentLineWidth = 0;
                y += AndroidUtilities.m9dp(44.0f);
            }
            if (maxWidth - allCurrentLineWidth < minWidth) {
                allY += AndroidUtilities.m9dp(44.0f);
            }
            GroupCreateActivity.this.editText.measure(MeasureSpec.makeMeasureSpec(maxWidth - currentLineWidth, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(32.0f), NUM));
            if (!this.animationStarted) {
                int currentHeight = allY + AndroidUtilities.m9dp(44.0f);
                int fieldX = currentLineWidth + AndroidUtilities.m9dp(16.0f);
                GroupCreateActivity.this.fieldY = y;
                if (this.currentAnimation != null) {
                    if (GroupCreateActivity.this.containerHeight != y + AndroidUtilities.m9dp(44.0f)) {
                        this.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[]{resultHeight}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationX() != ((float) fieldX)) {
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationX", new float[]{(float) fieldX}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationY() != ((float) GroupCreateActivity.this.fieldY)) {
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationY", new float[]{(float) GroupCreateActivity.this.fieldY}));
                    }
                    GroupCreateActivity.this.editText.setAllowDrawCursor(false);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    GroupCreateActivity.this.containerHeight = currentHeight;
                    GroupCreateActivity.this.editText.setTranslationX((float) fieldX);
                    GroupCreateActivity.this.editText.setTranslationY((float) GroupCreateActivity.this.fieldY);
                }
            } else if (!(this.currentAnimation == null || GroupCreateActivity.this.ignoreScrollEvent || this.removingSpan != null)) {
                GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(width, GroupCreateActivity.this.containerHeight);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span) {
            GroupCreateActivity.this.allSpans.add(span);
            GroupCreateActivity.this.selectedContacts.put(span.getUid(), span);
            GroupCreateActivity.this.editText.setHintVisible(false);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new CLASSNAME());
            this.currentAnimation.setDuration(150);
            this.addingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, 1.0f}));
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            GroupCreateActivity.this.ignoreScrollEvent = true;
            GroupCreateActivity.this.selectedContacts.remove(span.getUid());
            GroupCreateActivity.this.allSpans.remove(span);
            span.setOnClickListener(null);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(span);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                    if (GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                GroupCreateActivity.this.lambda$checkDiscard$70$PassportActivity();
            } else if (id == 1) {
                GroupCreateActivity.this.onDonePressed();
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$8 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1) {
                AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter */
    public class GroupCreateAdapter extends FastScrollAdapter {
        private ArrayList<User> contacts = new ArrayList();
        private Context context;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<User> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private boolean searching;

        public GroupCreateAdapter(Context ctx) {
            this.context = ctx;
            ArrayList<TL_contact> arrayList = ContactsController.getInstance(GroupCreateActivity.this.currentAccount).contacts;
            for (int a = 0; a < arrayList.size(); a++) {
                User user = MessagesController.getInstance(GroupCreateActivity.this.currentAccount).getUser(Integer.valueOf(((TL_contact) arrayList.get(a)).user_id));
                if (!(user == null || user.self || user.deleted)) {
                    this.contacts.add(user);
                }
            }
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(GroupCreateActivity.this) {
                public SparseArray getExcludeUsers() {
                    return SearchAdapterHelper$SearchAdapterHelperDelegate$$CC.getExcludeUsers(this);
                }

                public void onDataSetChanged() {
                    GroupCreateAdapter.this.notifyDataSetChanged();
                }

                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }
            });
        }

        public void setSearching(boolean value) {
            if (this.searching != value) {
                this.searching = value;
                notifyDataSetChanged();
            }
        }

        public String getLetter(int position) {
            if (position < 0 || position >= this.contacts.size()) {
                return null;
            }
            User user = (User) this.contacts.get(position);
            if (user == null) {
                return null;
            }
            if (LocaleController.nameDisplayOrder == 1) {
                if (!TextUtils.isEmpty(user.first_name)) {
                    return user.first_name.substring(0, 1).toUpperCase();
                }
                if (!TextUtils.isEmpty(user.last_name)) {
                    return user.last_name.substring(0, 1).toUpperCase();
                }
            } else if (!TextUtils.isEmpty(user.last_name)) {
                return user.last_name.substring(0, 1).toUpperCase();
            } else {
                if (!TextUtils.isEmpty(user.first_name)) {
                    return user.first_name.substring(0, 1).toUpperCase();
                }
            }
            return TtmlNode.ANONYMOUS_REGION_ID;
        }

        public int getItemCount() {
            if (!this.searching) {
                return this.contacts.size();
            }
            int count = this.searchResult.size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            if (globalCount != 0) {
                return count + (globalCount + 1);
            }
            return count;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GroupCreateSectionCell(this.context);
                    break;
                default:
                    view = new GroupCreateUserCell(this.context, true);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    GroupCreateSectionCell cell = (GroupCreateSectionCell) holder.itemView;
                    if (this.searching) {
                        cell.setText(LocaleController.getString("GlobalSearch", R.string.GlobalSearch));
                        return;
                    }
                    return;
                default:
                    User user;
                    GroupCreateUserCell cell2 = holder.itemView;
                    CharSequence username = null;
                    CharSequence name = null;
                    if (this.searching) {
                        int localCount = this.searchResult.size();
                        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
                        if (position >= 0 && position < localCount) {
                            user = (User) this.searchResult.get(position);
                        } else if (position <= localCount || position > globalCount + localCount) {
                            user = null;
                        } else {
                            user = (User) this.searchAdapterHelper.getGlobalSearch().get((position - localCount) - 1);
                        }
                        if (user != null) {
                            if (position < localCount) {
                                name = (CharSequence) this.searchResultNames.get(position);
                                if (!(name == null || TextUtils.isEmpty(user.username) || !name.toString().startsWith("@" + user.username))) {
                                    username = name;
                                    name = null;
                                }
                            } else if (position > localCount && !TextUtils.isEmpty(user.username)) {
                                String foundUserName = this.searchAdapterHelper.getLastFoundUsername();
                                if (foundUserName.startsWith("@")) {
                                    foundUserName = foundUserName.substring(1);
                                }
                                try {
                                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                    spannableStringBuilder.append("@");
                                    spannableStringBuilder.append(user.username);
                                    int index = user.username.toLowerCase().indexOf(foundUserName);
                                    if (index != -1) {
                                        int len = foundUserName.length();
                                        if (index == 0) {
                                            len++;
                                        } else {
                                            index++;
                                        }
                                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + len, 33);
                                    }
                                    Object username2 = spannableStringBuilder;
                                } catch (Exception e) {
                                    username2 = user.username;
                                }
                            }
                        }
                    } else {
                        user = (User) this.contacts.get(position);
                    }
                    cell2.setUser(user, name, username2);
                    cell2.setChecked(GroupCreateActivity.this.selectedContacts.indexOfKey(user.var_id) >= 0, false);
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (this.searching && position == this.searchResult.size()) {
                return 0;
            }
            return 1;
        }

        public int getPositionForScrollProgress(float progress) {
            return (int) (((float) getItemCount()) * progress);
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.itemView instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) holder.itemView).recycle();
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public void searchDialogs(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.queryServerSearch(null, true, false, false, false, 0, false);
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        GroupCreateAdapter.this.searchTimer.cancel();
                        GroupCreateAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m13e(e);
                    }
                    AndroidUtilities.runOnUIThread(new GroupCreateActivity$GroupCreateAdapter$2$$Lambda$0(this, query));
                }

                final /* synthetic */ void lambda$run$1$GroupCreateActivity$GroupCreateAdapter$2(String query) {
                    GroupCreateAdapter.this.searchAdapterHelper.queryServerSearch(query, true, false, true, false, 0, false);
                    Utilities.searchQueue.postRunnable(new GroupCreateActivity$GroupCreateAdapter$2$$Lambda$1(this, query));
                }

                final /* synthetic */ void lambda$null$0$GroupCreateActivity$GroupCreateAdapter$2(String query) {
                    String search1 = query.trim().toLowerCase();
                    if (search1.length() == 0) {
                        GroupCreateAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
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
                    ArrayList<User> resultArray = new ArrayList();
                    ArrayList<CharSequence> resultArrayNames = new ArrayList();
                    for (int a = 0; a < GroupCreateAdapter.this.contacts.size(); a++) {
                        User user = (User) GroupCreateAdapter.this.contacts.get(a);
                        String name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                        String tName = LocaleController.getInstance().getTranslitString(name);
                        if (name.equals(tName)) {
                            tName = null;
                        }
                        int found = 0;
                        int length = search.length;
                        int i = 0;
                        while (i < length) {
                            String q = search[i];
                            if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                                found = 1;
                            } else if (user.username != null && user.username.startsWith(q)) {
                                found = 2;
                            }
                            if (found != 0) {
                                if (found == 1) {
                                    resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                } else {
                                    resultArrayNames.add(AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q));
                                }
                                resultArray.add(user);
                            } else {
                                i++;
                            }
                        }
                    }
                    GroupCreateAdapter.this.updateSearchResults(resultArray, resultArrayNames);
                }
            }, 200, 300);
        }

        private void updateSearchResults(ArrayList<User> users, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new GroupCreateActivity$GroupCreateAdapter$$Lambda$0(this, users, names));
        }

        /* renamed from: lambda$updateSearchResults$0$GroupCreateActivity$GroupCreateAdapter */
        final /* synthetic */ void mo17073x38var_e(ArrayList users, ArrayList names) {
            this.searchResult = users;
            this.searchResultNames = names;
            notifyDataSetChanged();
        }
    }

    public GroupCreateActivity(Bundle args) {
        super(args);
        this.chatType = args.getInt("chatType", 0);
        this.isAlwaysShare = args.getBoolean("isAlwaysShare", false);
        this.isNeverShare = args.getBoolean("isNeverShare", false);
        this.isGroup = args.getBoolean("isGroup", false);
        this.chatId = args.getInt("chatId");
        this.showFabButton = args.getBoolean("showFabButton", false);
        this.maxCount = this.chatType == 0 ? MessagesController.getInstance(this.currentAccount).maxMegagroupCount : MessagesController.getInstance(this.currentAccount).maxBroadcastCount;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onClick(View v) {
        GroupCreateSpan span = (GroupCreateSpan) v;
        if (span.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(span);
            updateHint();
            checkVisibleRows();
            return;
        }
        if (this.currentDeletingSpan != null) {
            this.currentDeletingSpan.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = span;
        span.startDeleteAnimation();
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.doneButtonVisible = this.chatType == 2;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.chatType == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", R.string.ChannelAddMembers));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", R.string.AlwaysShareWithTitle));
            }
        } else if (!this.isNeverShare) {
            this.actionBar.setTitle(this.chatType == 0 ? LocaleController.getString("NewGroup", R.string.NewGroup) : LocaleController.getString("NewBroadcastList", R.string.NewBroadcastList));
        } else if (this.isGroup) {
            this.actionBar.setTitle(LocaleController.getString("NeverAllow", R.string.NeverAllow));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.fragmentView = new ViewGroup(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int maxSize;
                float f = 56.0f;
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                if (AndroidUtilities.isTablet() || height > width) {
                    maxSize = AndroidUtilities.m9dp(144.0f);
                } else {
                    maxSize = AndroidUtilities.m9dp(56.0f);
                }
                GroupCreateActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                GroupCreateActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                GroupCreateActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                if (GroupCreateActivity.this.floatingButton != null) {
                    if (VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    int w = AndroidUtilities.m9dp(f);
                    GroupCreateActivity.this.floatingButton.measure(MeasureSpec.makeMeasureSpec(w, NUM), MeasureSpec.makeMeasureSpec(w, NUM));
                }
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
                GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
                GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
                if (GroupCreateActivity.this.floatingButton != null) {
                    int l = LocaleController.isRTL ? AndroidUtilities.m9dp(14.0f) : ((right - left) - AndroidUtilities.m9dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredWidth();
                    int t = ((bottom - top) - AndroidUtilities.m9dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredHeight();
                    GroupCreateActivity.this.floatingButton.layout(l, t, GroupCreateActivity.this.floatingButton.getMeasuredWidth() + l, GroupCreateActivity.this.floatingButton.getMeasuredHeight() + t);
                }
            }

            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == GroupCreateActivity.this.listView || child == GroupCreateActivity.this.emptyView) {
                    GroupCreateActivity.this.parentLayout.drawHeaderShadow(canvas, GroupCreateActivity.this.scrollView.getMeasuredHeight());
                }
                return result;
            }
        };
        ViewGroup frameLayout = this.fragmentView;
        this.scrollView = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (GroupCreateActivity.this.ignoreScrollEvent) {
                    GroupCreateActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += GroupCreateActivity.this.fieldY + AndroidUtilities.m9dp(20.0f);
                rectangle.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.m9dp(50.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        frameLayout.addView(this.scrollView);
        this.spansContainer = new SpansContainer(context);
        this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new GroupCreateActivity$$Lambda$0(this));
        this.editText = new EditTextBoldCursor(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (GroupCreateActivity.this.currentDeletingSpan != null) {
                    GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateActivity.this.currentDeletingSpan = null;
                }
                return super.onTouchEvent(event);
            }
        };
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorColor(Theme.getColor(Theme.key_groupcreate_cursor));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(NUM);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        if (this.chatType == 2) {
            this.editText.setHintText(LocaleController.getString("AddMutual", R.string.AddMutual));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.editText.setHintText(LocaleController.getString("AlwaysAllowPlaceholder", R.string.AlwaysAllowPlaceholder));
            } else {
                this.editText.setHintText(LocaleController.getString("AlwaysShareWithPlaceholder", R.string.AlwaysShareWithPlaceholder));
            }
        } else if (!this.isNeverShare) {
            this.editText.setHintText(LocaleController.getString("SendMessageTo", R.string.SendMessageTo));
        } else if (this.isGroup) {
            this.editText.setHintText(LocaleController.getString("NeverAllowPlaceholder", R.string.NeverAllowPlaceholder));
        } else {
            this.editText.setHintText(LocaleController.getString("NeverShareWithPlaceholder", R.string.NeverShareWithPlaceholder));
        }
        this.editText.setCustomSelectionActionModeCallback(new CLASSNAME());
        this.editText.setOnEditorActionListener(new GroupCreateActivity$$Lambda$1(this));
        this.editText.setOnKeyListener(new CLASSNAME());
        this.editText.addTextChangedListener(new CLASSNAME());
        this.emptyView = new EmptyTextProgressView(context);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        frameLayout.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.listView = new RecyclerListView(context);
        this.listView.setFastScrollEnabled();
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        Adapter groupCreateAdapter = new GroupCreateAdapter(context);
        this.adapter = groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        recyclerListView = this.listView;
        ItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = groupCreateDividerItemDecoration;
        recyclerListView.addItemDecoration(groupCreateDividerItemDecoration);
        frameLayout.addView(this.listView);
        this.listView.setOnItemClickListener(new GroupCreateActivity$$Lambda$2(this));
        this.listView.setOnScrollListener(new CLASSNAME());
        if (this.showFabButton) {
            this.floatingButton = new ImageView(context);
            this.floatingButton.setScaleType(ScaleType.CENTER);
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.m9dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
            if (VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
                drawable = combinedDrawable;
            }
            this.floatingButton.setBackgroundDrawable(drawable);
            this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setArrowRotation(180);
            this.floatingButton.setImageDrawable(backDrawable);
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(2.0f), (float) AndroidUtilities.m9dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(2.0f)}).setDuration(200));
                this.floatingButton.setStateListAnimator(animator);
                this.floatingButton.setOutlineProvider(new CLASSNAME());
            }
            frameLayout.addView(this.floatingButton);
            this.floatingButton.setOnClickListener(new GroupCreateActivity$$Lambda$3(this));
            if (this.chatType != 2) {
                this.floatingButton.setVisibility(4);
                this.floatingButton.setScaleX(0.0f);
                this.floatingButton.setScaleY(0.0f);
                this.floatingButton.setAlpha(0.0f);
            }
        } else {
            this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
            if (this.chatType != 2) {
                this.doneButton.setScaleX(0.0f);
                this.doneButton.setScaleY(0.0f);
                this.doneButton.setAlpha(0.0f);
            }
        }
        updateHint();
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$GroupCreateActivity(View v) {
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    final /* synthetic */ boolean lambda$createView$1$GroupCreateActivity(TextView v, int actionId, KeyEvent event) {
        return actionId == 6 && onDonePressed();
    }

    final /* synthetic */ void lambda$createView$2$GroupCreateActivity(View view, int position) {
        boolean z = false;
        if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell cell = (GroupCreateUserCell) view;
            User user = cell.getUser();
            if (user != null) {
                boolean exists;
                if (this.selectedContacts.indexOfKey(user.var_id) >= 0) {
                    exists = true;
                } else {
                    exists = false;
                }
                if (exists) {
                    this.spansContainer.removeSpan((GroupCreateSpan) this.selectedContacts.get(user.var_id));
                } else if (this.maxCount != 0 && this.selectedContacts.size() == this.maxCount) {
                    return;
                } else {
                    if (this.chatType == 0 && this.selectedContacts.size() == MessagesController.getInstance(this.currentAccount).maxGroupCount) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("SoftUserLimitAlert", R.string.SoftUserLimitAlert));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        showDialog(builder.create());
                        return;
                    }
                    boolean z2;
                    MessagesController instance = MessagesController.getInstance(this.currentAccount);
                    if (this.searching) {
                        z2 = false;
                    } else {
                        z2 = true;
                    }
                    instance.putUser(user, z2);
                    GroupCreateSpan span = new GroupCreateSpan(this.editText.getContext(), user);
                    this.spansContainer.addSpan(span);
                    span.setOnClickListener(this);
                }
                updateHint();
                if (this.searching || this.searchWas) {
                    AndroidUtilities.showKeyboard(this.editText);
                } else {
                    if (!exists) {
                        z = true;
                    }
                    cell.setChecked(z, true);
                }
                if (this.editText.length() > 0) {
                    this.editText.setText(null);
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.editText != null) {
            this.editText.requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoad) {
            if (this.emptyView != null) {
                this.emptyView.showTextView();
            }
            if (this.adapter != null) {
                this.adapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int mask = ((Integer) args[0]).intValue();
                int count = this.listView.getChildCount();
                if ((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) {
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.chatDidCreated) {
            lambda$null$10$ProfileActivity();
        }
    }

    @Keep
    public void setContainerHeight(int value) {
        this.containerHeight = value;
        if (this.spansContainer != null) {
            this.spansContainer.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    private void checkVisibleRows() {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) child;
                User user = cell.getUser();
                if (user != null) {
                    boolean z;
                    if (this.selectedContacts.indexOfKey(user.var_id) >= 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    cell.setChecked(z, true);
                }
            }
        }
    }

    private boolean onDonePressed() {
        int a;
        if (this.chatType == 2) {
            ArrayList<InputUser> result = new ArrayList();
            for (a = 0; a < this.selectedContacts.size(); a++) {
                InputUser user = MessagesController.getInstance(this.currentAccount).getInputUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedContacts.keyAt(a))));
                if (user != null) {
                    result.add(user);
                }
            }
            MessagesController.getInstance(this.currentAccount).addUsersToChannel(this.chatId, result, null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            Bundle args2 = new Bundle();
            args2.putInt("chat_id", this.chatId);
            presentFragment(new ChatActivity(args2), true);
        } else if (!this.doneButtonVisible || this.selectedContacts.size() == 0) {
            return false;
        } else {
            ArrayList<Integer> result2 = new ArrayList();
            for (a = 0; a < this.selectedContacts.size(); a++) {
                result2.add(Integer.valueOf(this.selectedContacts.keyAt(a)));
            }
            if (this.isAlwaysShare || this.isNeverShare) {
                if (this.delegate != null) {
                    this.delegate.didSelectUsers(result2);
                }
                lambda$checkDiscard$70$PassportActivity();
            } else {
                Bundle args = new Bundle();
                args.putIntegerArrayList("result", result2);
                args.putInt("chatType", this.chatType);
                presentFragment(new GroupCreateFinalActivity(args));
            }
        }
        return true;
    }

    private void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.itemDecoration.setSearching(false);
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
    }

    private void updateHint() {
        if (!(this.isAlwaysShare || this.isNeverShare)) {
            if (this.chatType == 2) {
                this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
            } else if (this.selectedContacts.size() == 0) {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", R.string.MembersCountZero, LocaleController.formatPluralString("Members", this.maxCount)));
            } else {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", R.string.MembersCount, Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount)));
            }
        }
        if (this.chatType == 2) {
            return;
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.doneButtonVisible && this.allSpans.isEmpty()) {
            if (this.currentDoneButtonAnimation != null) {
                this.currentDoneButtonAnimation.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            if (this.doneButton != null) {
                animatorSet = this.currentDoneButtonAnimation;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[]{0.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
            } else if (this.floatingButton != null) {
                animatorSet = this.currentDoneButtonAnimation;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButton, "scaleX", new float[]{0.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, "scaleY", new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.floatingButton, "alpha", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
                this.currentDoneButtonAnimation.addListener(new CLASSNAME());
            }
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = false;
        } else if (!this.doneButtonVisible && !this.allSpans.isEmpty()) {
            if (this.currentDoneButtonAnimation != null) {
                this.currentDoneButtonAnimation.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            if (this.doneButton != null) {
                animatorSet = this.currentDoneButtonAnimation;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[]{1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[]{1.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else if (this.floatingButton != null) {
                this.floatingButton.setVisibility(0);
                animatorSet = this.currentDoneButtonAnimation;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButton, "scaleX", new float[]{1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, "scaleY", new float[]{1.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.floatingButton, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = true;
        }
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new GroupCreateActivity$$Lambda$4(this);
        r10 = new ThemeDescription[38];
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r10[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r10[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r10[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_groupcreate_hintText);
        r10[16] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_groupcreate_cursor);
        r10[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, Theme.key_graySection);
        r10[18] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, Theme.key_groupcreate_sectionShadow);
        r10[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r10[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r10[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkbox);
        r10[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkboxCheck);
        r10[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_onlineText);
        r10[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_offlineText);
        r10[25] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r10[26] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        r10[27] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        r10[28] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        r10[29] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        r10[30] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        r10[31] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        r10[32] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        r10[33] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundGroupCreateSpanBlue);
        r10[34] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanBackground);
        r10[35] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanText);
        r10[36] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanDelete);
        r10[37] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundBlue);
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$4$GroupCreateActivity() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child).update(0);
                }
            }
        }
    }
}
