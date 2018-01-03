package ua.softserve.academy.kv030.authservice.exceptions;


import java.util.Arrays;

public class AuthServiceException extends RuntimeException {

    public AuthServiceException() {
    }

    public AuthServiceException(String message){
        super(message);
    }

    public String getAuthServiceStackTrace() {
        StringBuilder trace = new StringBuilder();
        StackTraceElement[] traceElements = super.getStackTrace();
        Arrays.stream(traceElements).forEach(x-> trace.append(x.toString()+"\n"));
        return trace.toString();
    }
}
