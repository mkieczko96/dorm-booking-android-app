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
import org.greenrobot.greendao.annotation.ToOne;

@Entity(
        active = true,
        nameInDb = "BOOKING",
        createInDb = true,
        generateConstructors = true,
        generateGettersSetters = true
)
public class Booking {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private Long facilityId;

    @ToOne(joinProperty = "facilityId")
    private Facility facility;

    @NotNull
    private Long userId;

    @ToOne(joinProperty = "userId")
    private User user;

    @NotNull
    private Long beginAt;

    @NotNull
    @Property(nameInDb = "DURATION")
    private long durationInMinutes;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1550763038)
    private transient BookingDao myDao;

    @Generated(hash = 1715054857)
    public Booking(Long id, @NotNull Long facilityId, @NotNull Long userId,
                   @NotNull Long beginAt, long durationInMinutes) {
        this.id = id;
        this.facilityId = facilityId;
        this.userId = userId;
        this.beginAt = beginAt;
        this.durationInMinutes = durationInMinutes;
    }

    @Generated(hash = 834494427)
    public Booking() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFacilityId() {
        return this.facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBeginAt() {
        return this.beginAt;
    }

    public void setBeginAt(Long beginAt) {
        this.beginAt = beginAt;
    }

    public long getDurationInMinutes() {
        return this.durationInMinutes;
    }

    public void setDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    @Generated(hash = 1508989291)
    private transient Long facility__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 147836672)
    public Facility getFacility() {
        Long __key = this.facilityId;
        if (facility__resolvedKey == null || !facility__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FacilityDao targetDao = daoSession.getFacilityDao();
            Facility facilityNew = targetDao.load(__key);
            synchronized (this) {
                facility = facilityNew;
                facility__resolvedKey = __key;
            }
        }
        return facility;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059611158)
    public void setFacility(@NotNull Facility facility) {
        if (facility == null) {
            throw new DaoException(
                    "To-one property 'facilityId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.facility = facility;
            facilityId = facility.getId();
            facility__resolvedKey = facilityId;
        }
    }

    @Generated(hash = 251390918)
    private transient Long user__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 859885876)
    public User getUser() {
        Long __key = this.userId;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
                user__resolvedKey = __key;
            }
        }
        return user;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 462495677)
    public void setUser(@NotNull User user) {
        if (user == null) {
            throw new DaoException(
                    "To-one property 'userId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.user = user;
            userId = user.getId();
            user__resolvedKey = userId;
        }
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
    @Generated(hash = 1269455487)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBookingDao() : null;
    }
}

