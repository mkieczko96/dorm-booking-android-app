package com.dormbooker.data.dao;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.dormbooker.data.User;
import com.dormbooker.data.dao.UserDao;

public class UserTest extends AbstractDaoTestLongPk<UserDao, User> {

    public UserTest() {
        super(UserDao.class);
    }

    @Override
    protected User createEntity(Long key) {
        User entity = new User();
        entity.setId(key);
        entity.setFirstName();
        entity.setLastName();
        entity.setRoomNumber();
        entity.setEmailAddress();
        return entity;
    }

}
