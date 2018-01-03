package ua.softserve.academy.kv030.authservice.services.scheduledService;

/**
 * this interface represents methods for scheduled methods such as
 * deleting expiring files from database and posting to twitter
 */
public interface ScheduledService {

    void cleanExpiredFiles();

    void postRegisteredUsersInTwitter();

    void postSavedFilesInTwitter();
}
