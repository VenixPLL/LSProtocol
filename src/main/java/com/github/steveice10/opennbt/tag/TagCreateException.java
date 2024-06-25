package com.github.steveice10.opennbt.tag;

/**
 * An exception thrown when an error occurs while created a tag instance.
 */
public class TagCreateException extends Exception {
    private static final long serialVersionUID = -2022049594558041160L;

    public TagCreateException() {
        super();
    }

    public TagCreateException(String message) {
        super(message);
    }

    public TagCreateException(Throwable cause) {
        super(cause);
    }

    public TagCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
