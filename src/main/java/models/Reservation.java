package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    private final String reservationId;
    private final Patron patron;
    private final LibraryItem item;
    private final LocalDateTime reservationDate;
    private ReservationStatus status;

    public Reservation(Patron patron, LibraryItem item) {
        this.reservationId = UUID.randomUUID().toString();
        this.patron = patron;
        this.item = item;
        this.reservationDate = LocalDateTime.now();
        this.status = ReservationStatus.PENDING;
    }

    public String getReservationId() { return reservationId; }
    public Patron getPatron() { return patron; }
    public LibraryItem getItem() { return item; }
    public LocalDateTime getReservationDate() { return reservationDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}