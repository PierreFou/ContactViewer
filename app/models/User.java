package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {

    public long uid;
    public String access_token;

    public User(long uid) {
        this.uid = uid;
    }

}
