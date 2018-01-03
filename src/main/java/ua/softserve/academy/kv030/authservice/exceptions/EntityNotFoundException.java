package ua.softserve.academy.kv030.authservice.exceptions;

public class EntityNotFoundException extends AuthServiceException {
    private static String generalMessage = "No such entity in database.";
    protected String entityId;

    public EntityNotFoundException() {
        super(generalMessage);
    }

    public EntityNotFoundException(String message) {
        super(generalMessage + " " + message);
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
