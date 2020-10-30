package com.dormbooker.data.dao;

import com.dormbooker.data.Booking;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class BookingTest extends AbstractDaoTestLongPk<BookingDao, Booking> {

    public BookingTest() {
        super(BookingDao.class);
    }

    @Override
    protected Booking createEntity(Long key) {
        Booking entity = new Booking();
        entity.setId(key);
        entity.setFacilityId(1L);
        entity.setUserId(1L);
        entity.setBeginAt(1L);
        entity.setDurationInMinutes(30);
        return entity;
    }

}
