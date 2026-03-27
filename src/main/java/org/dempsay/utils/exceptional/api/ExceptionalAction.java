package org.dempsay.utils.exceptional.api;

import java.util.Objects;

/**
 * Utility class for executing actions
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <sdempsay@pavlovmedia.com>}
 */
public final class ExceptionalAction {
    private final ExceptionalActionCall exceptionalAction;
    private ExceptionalListener exceptionalListener;

    private ExceptionalAction(final ExceptionalActionCall action) {
        this.exceptionalAction = action;
    }

    public ExceptionalAction with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

    @SuppressWarnings("checkstyle:illegalcatch")
    public boolean execute() {
        try {
            this.exceptionalAction.action();
            return true;
        } catch (Exception e) {
            if (Objects.nonNull(this.exceptionalListener)) {
                exceptionalListener.onError(e);
            }
            return false;
        }
    }

    public static ExceptionalAction of(final ExceptionalActionCall action) {
        return new ExceptionalAction(action);
    }
}
