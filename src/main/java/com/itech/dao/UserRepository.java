package com.itech.dao;

import com.itech.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserRepository extends EntityRepository<User>{
    @Override
    public String getTableName() {
        return "users";
    }
}
