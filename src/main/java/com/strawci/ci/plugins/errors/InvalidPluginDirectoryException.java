package com.strawci.ci.plugins.errors;

public class InvalidPluginDirectoryException extends Exception  {
    public InvalidPluginDirectoryException(final String cause) {
        super(cause);
    }
}
