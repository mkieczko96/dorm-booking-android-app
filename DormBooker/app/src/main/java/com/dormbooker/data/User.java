package com.dormbooker.data;

import com.dormbooker.data.dao.BookingDao;
import com.dormbooker.data.dao.DaoSession;
import com.dormbooker.data.dao.FacilityDao;
import com.dormbooker.data.dao.UserDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        active = true,
        nameInDb = "USER",
        createInDb = true,
        generateConstructors = true,
        generateGettersSetters = true
)
public class User {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Property(nameInDb = "ROOM")
    private Long roomNumber;

    @NotNull
    private String emailAddress;

    @ToMany(referencedJoinProperty = "userId")
    private List<Booking> bookings;

    @ToMany(referencedJoinProperty = "adminId")
    private List<Facility> facilities;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 208466033)
    public User(Long id, @NotNull String firstName, @NotNull String lastName,
                @NotNull Long roomNumber, @NotNull String emailAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roomNumber = roomNumber;
        this.emailAddress = emailAddress;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getRoomNumber() {
        return this.roomNumber;
    }

    public void setRoomNumber(Long roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 77226949)
    public List<Booking> getBookings() {
        if (bookings == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookingDao targetDao = daoSession.getBookingDao();
            List<Booking> bookingsNew = targetDao._queryUser_Bookings(id);
            synchronized (this) {
                if (bookings == null) {
                    bookings = bookingsNew;
                }
            }
        }
        return bookings;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 99037841)
    public synchronized void resetBookings() {
        bookings = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 577567342)
    public List<Facility> getFacilities() {
        if (facilities == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FacilityDao targetDao = daoSession.getFacilityDao();
            List<Facility> facilitiesNew = targetDao._queryUser_Facilities(id);
            synchronized (this) {
                if (facilities == null) {
                    facilities = facilitiesNew;
                }
            }
        }
        return facilities;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 204846972)
    public synchronized void resetFacilities() {
        facilities = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }
}
