package com.github.steveice10.opennbt.conversion;

/**
 * An exception thrown when an error occurs while converting something.
 */
public class ConversionException extends RuntimeException {
    private static final long serialVersionUID = -2022049594558041160L;

    public ConversionException() {
        super();
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
