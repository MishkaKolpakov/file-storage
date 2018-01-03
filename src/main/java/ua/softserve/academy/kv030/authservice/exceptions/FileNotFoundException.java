package ua.softserve.academy.kv030.authservice.exceptions;

public class FileNotFoundException extends AuthServiceException {
    private String id;
    private static String message = "File not found ";

    public FileNotFoundException() {
        super();
    }

    public FileNotFoundException(String id) {
        this(message,id);
    }
    public FileNotFoundException(String message,String id) {
        super(String.format("%s with id: %s",message,id));
    }

    public String getId() {
        return id;
    }
}
