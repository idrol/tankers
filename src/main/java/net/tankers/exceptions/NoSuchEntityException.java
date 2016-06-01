package net.tankers.exceptions;

/**
 * Created by local-admin on 30-05-2016.
 */
public class NoSuchEntityException extends Exception{

    public NoSuchEntityException() {}

    public NoSuchEntityException(String message) { super(message); }

    public NoSuchEntityException(Throwable cause) { super(cause); }

    public NoSuchEntityException(String message, Throwable cause) { super(message, cause); }
}
