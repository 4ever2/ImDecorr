package com.github.imdecorr;

import org.scijava.Cancelable;
import org.scijava.command.Command;

public abstract class CancelableCommand implements Cancelable, Command {
    /** Reason for cancelation, or null if not canceled. */
    private String cancelReason;

    @Override
    public boolean isCanceled() {
        return cancelReason != null;
    }

    /** Cancels the command execution, with the given reason for doing so. */
    @Override
    public void cancel(final String reason) {
        cancelReason = reason == null ? "" : reason;
    }

    @Override
    public String getCancelReason() {
        return cancelReason;
    }
}
