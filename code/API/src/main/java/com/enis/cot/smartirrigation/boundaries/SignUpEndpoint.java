package com.enis.cot.smartirrigation.boundaries;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.enis.cot.smartirrigation.util.Argon2Utility;
@ApplicationScoped
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SignUpEndpoint {
    @Inject
    private MongoClient mongoClient;
    @POST
    public Response save(String json) {
        org.json.JSONObject obj = new org.json.JSONObject(json);
        String mail = obj.getString("mail");
        String userName = obj.getString("userName");
        String password = obj.getString("password");
        Long permissionLevel = obj.optLong("permissionLevel", 1L);
        MongoDatabase db = mongoClient.getDatabase("Smartirrigation");
        MongoCollection<Document> col = db.getCollection("User");
        if (col.find(new Document("_id", mail)).first() != null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("user already exists").build();
        }
        String hash = Argon2Utility.hash(password.toCharArray());
        Document doc = new Document("_id", mail).append("mail", mail).append("userName", userName).append("password", hash).append("permissionLevel", permissionLevel);
        col.insertOne(doc);
        return Response.ok().entity("user created").build();
    }
}
