package observer;

import models.Patron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatronNotificationObserver implements NotificationObserver {
    private static final Logger logger = LoggerFactory.getLogger(PatronNotificationObserver.class);
    private final Patron patron;

    public PatronNotificationObserver(Patron patron) {
        this.patron = patron;
    }

    @Override
    public void update(String message) {
        logger.info("Notification for patron {}: {}", patron.getName(), message);
        System.out.println("Dear " + patron.getName() + ", " + message);
    }

    @Override
    public Patron getPatron() {
        return patron;
    }
}