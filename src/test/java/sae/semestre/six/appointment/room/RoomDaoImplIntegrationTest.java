package sae.semestre.six.appointment.room;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RoomDaoImplIntegrationTest {

    @Autowired
    private RoomDao roomDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testFindByRoomNumber_ExistingRoom() {
        // Given
        Room room = new Room();
        room.setRoomNumber("A101");
        room.setFloor(1);
        room.setType("Consultation");
        room.setCapacity(5);
        entityManager.persist(room);
        entityManager.flush();

        // When
        Room foundRoom = roomDao.findByRoomNumber("A101");

        // Then
        assertNotNull(foundRoom, "The room should be found");
        assertEquals("A101", foundRoom.getRoomNumber(), "The room number should match");
        assertEquals(1, foundRoom.getFloor(), "The floor should match");
        assertEquals("Consultation", foundRoom.getType(), "The type should match");
    }

    @Test
    void testFindByRoomNumber_NonExistingRoom() {
        // Given a room number that does not exist
        String nonExistingRoomNumber = "Z999";

        // When & Then
        assertThrows(EmptyResultDataAccessException.class,
                () -> roomDao.findByRoomNumber(nonExistingRoomNumber),
                "A EmptyResultDataAccessException exception should be throw for a non existent room"
        );
    }

    @Test
    void testCreate() {
        // Given
        Room room = new Room();
        room.setRoomNumber("B202");
        room.setFloor(2);
        room.setType("Opération");
        room.setCapacity(3);

        // When
        roomDao.save(room);

        // Then the room should be saved
        assertNotNull(room.getId(), "ID should not be null after creation");

        Room retrievedRoom = entityManager.find(Room.class, room.getId());
        assertNotNull(retrievedRoom, "The room should be found");
        assertEquals("B202", retrievedRoom.getRoomNumber());
        assertEquals(2, retrievedRoom.getFloor());
        assertEquals("Opération", retrievedRoom.getType());
        assertEquals(3, retrievedRoom.getCapacity());
    }

    @Test
    void testDelete() {
        // Given
        Room room = new Room();
        room.setRoomNumber("D404");
        entityManager.persist(room);
        entityManager.flush();
        Long roomId = room.getId();

        // When
        roomDao.delete(room);

        // Then
        Room deletedRoom = entityManager.find(Room.class, roomId);
        assertNull(deletedRoom, "The room should be deleted");
    }
}