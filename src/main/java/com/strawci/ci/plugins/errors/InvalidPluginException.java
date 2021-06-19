package com.strawci.ci.plugins.errors;

public class InvalidPluginException extends Exception {
    public InvalidPluginException(final Throwable cause) {
        super(cause);
    }

    public InvalidPluginException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidPluginException(final String message) {
        super(message);
    }
}