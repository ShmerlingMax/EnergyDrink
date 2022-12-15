package mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoClientImpl;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoClientt {

    public static void main(String[] args) {
        //127.0.0.1:27017 UBUNTU LOCALHOST
        MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);
        System.out.println("ASdasfd");
        MongoDatabase database = mongoClient.getDatabase("test");
        System.out.println(database.getName());
        MongoCollection<Document> a =database.getCollection("brands");

        a.insertOne(new Document("json", "{\"brands2\" : [\"adrik2\", \"monster2\"]}").append("id", 2));
        mongoClient.close();
    }
}
