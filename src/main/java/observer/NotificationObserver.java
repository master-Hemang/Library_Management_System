package observer;

import models.Patron;

public interface NotificationObserver {
    void update(String message);
    Patron getPatron();
}