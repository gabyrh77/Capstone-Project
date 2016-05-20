package com.nanodegree.gaby.bakerylovers.backend.db;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/** The Objectify object model for device registrations we are persisting */
@Entity
public class RegistrationRecord {

    @Id
    Long id;

    @Index
    private String regId;
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<UserRecord> user;

    public RegistrationRecord() {}

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public Key<UserRecord> getUser() {
        return user;
    }

    public void setUser(Key<UserRecord> user) {
        this.user = user;
    }

    public Long getUserId() {
        return user==null?null:user.getId();
    }
}