package com.enis.cot.smartirrigation.controllers;
import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.enis.cot.smartirrigation.entities.User;
import com.enis.cot.smartirrigation.repositories.UserRepository;
import com.enis.cot.smartirrigation.util.Argon2Utility;
@Stateless
@LocalBean
public class UserManager {
    @Inject
    private UserRepository userRepository;
    @Inject
    private MongoClient mongoClient;
    private User findUserByMail(String mail) {
        MongoDatabase db = mongoClient.getDatabase("Smartirrigation");
        MongoCollection<Document> col = db.getCollection("User");
        Document doc = col.find(new Document("_id", mail)).first();
        if (doc == null) throw new EJBException("User not found");
        return new User(doc.getString("mail"), doc.getString("userName"), doc.getString("password"), doc.getLong("permissionLevel"));
    }
    public User findByUsername(String mail) { return findUserByMail(mail); }
    public User authenticate(final String mail, final String password) throws EJBException {
        User user = findUserByMail(mail);
        if (Argon2Utility.check(user.getpassword(), password.toCharArray())) return user;
        throw new EJBException("Wrong password");
    }
    public User authenticateadmin(final String mail, final String password) throws EJBException {
        User user = findUserByMail(mail);
        if (Argon2Utility.check(user.getpassword(), password.toCharArray()) && user.getPermissionLevel().equals(2L)) return user;
        throw new EJBException("Wrong password or not admin");
    }
}
