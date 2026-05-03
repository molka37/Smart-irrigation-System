package com.enis.cot.smartirrigation.entities;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import jakarta.json.bind.annotation.JsonbVisibility;
import com.enis.cot.smartirrigation.util.Identity;

import java.io.Serializable;
import java.util.Objects;

@Entity("User") // FIX 1: nom de collection explicite pour JNoSQL
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class User implements Serializable, Identity {

    @Id
    private String mail;
    @Column
    private String userName;
    @Column
    private String password;
    @Column
    private Long permissionLevel;

    public User() {
    }

    public User(String mail, String userName, String password, Long permissionLevel) {
        this.mail = mail;
        this.userName = userName;
        this.password = password;
        this.permissionLevel = permissionLevel;
    }

    // FIX 2: getters renommés en convention JavaBeans standard
    public String getMail() {
        return mail;
    }

    // Gardés pour compatibilité avec le code existant
    public String getmail() {
        return mail;
    }

    public String getUserName() {
        return userName;
    }

    public String getuserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getpassword() {
        return password;
    }

    public Long getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(Long permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return true; // FIX 3: était "instanceof Sensor" — bug critique
        User user = (User) o;
        return Objects.equals(mail, user.mail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mail);
    }

    @Override
    public String getName() {
        return getmail();
    }

    @Override
    public String toString() {
        return "User{" +
                "mail='" + mail + '\'' +
                ", userName=" + userName +
                '}';
    }
}
