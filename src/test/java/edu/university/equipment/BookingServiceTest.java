package edu.university.equipment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingServiceTest {

    private BookingService service;

    @BeforeEach
    void setup() {
        service = new BookingService();
    }

    @Test
    void successfulBookingReducesInventory() {
        service.create("STU1001", "LAPTOP01", 2);
        assertEquals(8, service.getEquipment("LAPTOP01").getAvailableQuantity());
    }

    @Test
    void zeroQuantityRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create("STU1001", "LAPTOP01", 0));

        assertEquals(10, service.getEquipment("LAPTOP01").getAvailableQuantity());
    }

    @Test
    void nullBorrowerRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(null, "LAPTOP01", 1));
    
        assertEquals(10, service.getEquipment("LAPTOP01").getAvailableQuantity());
    }
    
    @Test
    void emptyBorrowerRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create("", "LAPTOP01", 1));
    
        assertEquals(10, service.getEquipment("LAPTOP01").getAvailableQuantity());
    }
    
    @Test
    void whitespaceOnlyBorrowerRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create("   ", "LAPTOP01", 1));
    
        assertEquals(10, service.getEquipment("LAPTOP01").getAvailableQuantity());
    }

    @Test
    void unknownEquipmentRejected() {
        assertThrows(NoSuchElementException.class,
                () -> service.create("STU1001", "UNKNOWN", 1));
    }

    @Test
    void ordinaryOverbookingRejected() {
        assertThrows(IllegalStateException.class,
                () -> service.create("STU1001", "PROJECTOR01", 6));

        assertEquals(4, service.getEquipment("PROJECTOR01").getAvailableQuantity());
    }

    @Test
    void exactAvailableInventoryCanBeBooked() {
        Booking booking = service.create("STU1001", "PROJECTOR01", 4);

        assertNotNull(booking);
        assertEquals(0, service.getEquipment("PROJECTOR01").getAvailableQuantity());
        assertEquals("STU1001", booking.getBorrowerId());
    }

    @Test
    void exceedingAvailableInventoryDoesNotChangeInventory() {
        assertThrows(IllegalStateException.class,
                () -> service.create("STU1001", "PROJECTOR01", 5));

        assertEquals(4, service.getEquipment("PROJECTOR01").getAvailableQuantity());
    }

    @Test
    void cancellationRestoresInventoryOnce() {
        Booking booking = service.create("STU1001", "TABLET01", 3);

        service.cancel(booking.getId());

        assertEquals(8, service.getEquipment("TABLET01").getAvailableQuantity());

        assertThrows(IllegalStateException.class,
                () -> service.cancel(booking.getId()));
    }
}
