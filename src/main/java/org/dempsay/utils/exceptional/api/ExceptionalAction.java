package org.dempsay.utils.exceptional.api;

import java.util.Objects;

/**
 * Utility class for executing actions
 *
 * @since 1.0.0
 * @author Shawn Dempsay {@literal <shawn@dempsay.org>}
 */
public final class ExceptionalAction {
    private final ExceptionalActionCall exceptionalAction;
    private ExceptionalListener exceptionalListener;
    private Runnable always;

    private ExceptionalAction(final ExceptionalActionCall action) {
        this.exceptionalAction = action;
    }

    /**
     * Sets an error listener to be called if an exception occurs during execution.
     *
     * @param exceptionalListener the listener to receive exceptions
     * @return this instance for method chaining
     * @since 1.0.0
     */
    public ExceptionalAction with(final ExceptionalListener exceptionalListener) {
        this.exceptionalListener = exceptionalListener;
        return this;
    }

    /**
     * Has a runnable action that always gets called, if there was an error or not.
     * this is similar to adding something to with, and doing a check for success
     * 
     * @param always
     * @return this instance for method chaining
     * @since 1.0.7
     */
    public ExceptionalAction always(final Runnable always) {
        this.always = always;
        return this;
    }

    /**
     * Executes the action, returning true on success or false if an exception occurred.
     *
     * @return true if the action completed successfully, false if an exception was thrown
     * @since 1.0.0
     */
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
        } finally {
            if (Objects.nonNull(this.always)) {
                always.run();
            }
        }
    }

    /**
     * Creates an ExceptionalAction from an ExceptionalActionCall.
     *
     * @param action the action to wrap
     * @return new ExceptionalAction instance
     * @since 1.0.0
     */
    public static ExceptionalAction of(final ExceptionalActionCall action) {
        return new ExceptionalAction(action);
    }
}
