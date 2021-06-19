package com.strawci.ci.plugins.errors;

public class InvalidDescriptorException extends Exception  {
    public InvalidDescriptorException(final Throwable cause, final String message) {
        super(message, cause);
    }

    public InvalidDescriptorException(final Throwable cause) {
        super("Invalid straw-plugin.yml", cause);
    }

    public InvalidDescriptorException(final String message) {
        super(message);
    }

    public InvalidDescriptorException() {
        super("Invalid straw-plugin.yml");
    }
}
