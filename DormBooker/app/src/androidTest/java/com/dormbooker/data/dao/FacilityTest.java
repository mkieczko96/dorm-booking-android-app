package com.dormbooker.data.dao;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.dormbooker.data.Facility;
import com.dormbooker.data.dao.FacilityDao;

public class FacilityTest extends AbstractDaoTestLongPk<FacilityDao, Facility> {

    public FacilityTest() {
        super(FacilityDao.class);
    }

    @Override
    protected Facility createEntity(Long key) {
        Facility entity = new Facility();
        entity.setId(key);
        entity.setName();
        entity.setFloor();
        entity.setAdminId();
        return entity;
    }

}
