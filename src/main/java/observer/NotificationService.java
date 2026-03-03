package observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationService {
    private Map<String, List<NotificationObserver>> itemObservers;

    public NotificationService() {
        this.itemObservers = new ConcurrentHashMap<>();
    }

    public void subscribe(String itemId, NotificationObserver observer) {
        itemObservers.computeIfAbsent(itemId, k -> new ArrayList<>()).add(observer);
    }

    public void unsubscribe(String itemId, NotificationObserver observer) {
        List<NotificationObserver> observers = itemObservers.get(itemId);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    public void notifyObservers(String itemId, String message) {
        List<NotificationObserver> observers = itemObservers.get(itemId);
        if (observers != null) {
            observers.forEach(observer -> observer.update(message));
        }
    }
}