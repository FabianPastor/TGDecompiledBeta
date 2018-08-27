package org.telegram.ui.Adapters;

import java.util.Comparator;
import org.telegram.messenger.EmojiSuggestion;

final /* synthetic */ class MentionsAdapter$$Lambda$5 implements Comparator {
    static final Comparator $instance = new MentionsAdapter$$Lambda$5();

    private MentionsAdapter$$Lambda$5() {
    }

    public int compare(Object obj, Object obj2) {
        return MentionsAdapter.lambda$searchUsernameOrHashtag$6$MentionsAdapter((EmojiSuggestion) obj, (EmojiSuggestion) obj2);
    }
}
