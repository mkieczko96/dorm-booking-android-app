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
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

@Entity(
        active = true,
        nameInDb = "FACILITY",
        createInDb = true,
        generateConstructors = true,
        generateGettersSetters = true
)
public class Facility {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Long floor;

    @Property(nameInDb = "DEFAULT_BOOKING_DURATION")
    private Long defaultBookingDurationInMinutes;

    @NotNull
    private Long adminId;

    @NotNull
    @ToOne(joinProperty = "adminId")
    private User admin;

    @ToMany(referencedJoinProperty = "facilityId")
    @OrderBy("beginAt ASC")
    private List<Booking> bookings;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2017034215)
    private transient FacilityDao myDao;

    @Generated(hash = 204249039)
    public Facility(Long id, @NotNull String name, @NotNull Long floor,
                    Long defaultBookingDurationInMinutes, @NotNull Long adminId) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.defaultBookingDurationInMinutes = defaultBookingDurationInMinutes;
        this.adminId = adminId;
    }

    @Generated(hash = 1036448758)
    public Facility() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFloor() {
        return this.floor;
    }

    public void setFloor(Long floor) {
        this.floor = floor;
    }

    public Long getDefaultBookingDurationInMinutes() {
        return this.defaultBookingDurationInMinutes;
    }

    public void setDefaultBookingDurationInMinutes(
            Long defaultBookingDurationInMinutes) {
        this.defaultBookingDurationInMinutes = defaultBookingDurationInMinutes;
    }

    public Long getAdminId() {
        return this.adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    @Generated(hash = 2048802654)
    private transient Long admin__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 267321358)
    public User getAdmin() {
        Long __key = this.adminId;
        if (admin__resolvedKey == null || !admin__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User adminNew = targetDao.load(__key);
            synchronized (this) {
                admin = adminNew;
                admin__resolvedKey = __key;
            }
        }
        return admin;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 949792772)
    public void setAdmin(@NotNull User admin) {
        if (admin == null) {
            throw new DaoException(
                    "To-one property 'adminId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.admin = admin;
            adminId = admin.getId();
            admin__resolvedKey = adminId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1212855303)
    public List<Booking> getBookings() {
        if (bookings == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookingDao targetDao = daoSession.getBookingDao();
            List<Booking> bookingsNew = targetDao._queryFacility_Bookings(id);
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
    @Generated(hash = 341049285)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFacilityDao() : null;
    }
}
