package com.example.yan_c_000.auth;

import dagger.Component;

/**
 * Created by yan-c_000 on 10.11.2017.
 */

@Component(modules =  UserFirebaseModule.class )
public interface UserDBApplication {

    User getUsers();

}
