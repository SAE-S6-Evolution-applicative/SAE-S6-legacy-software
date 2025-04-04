package sae.semestre.six.appointment.room;

import sae.semestre.six.generic.GenericDao;

public interface RoomDao extends GenericDao<Room, Long> {
    Room findByRoomNumber(String roomNumber);
} 