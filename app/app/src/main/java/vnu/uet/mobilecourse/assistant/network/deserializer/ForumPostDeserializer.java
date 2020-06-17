package vnu.uet.mobilecourse.assistant.network.deserializer;

import com.google.gson.*;
import vnu.uet.mobilecourse.assistant.model.forum.Post;

import java.lang.reflect.Type;

public class ForumPostDeserializer implements JsonDeserializer<Post> {
    @Override
    public Post deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Post post = new Gson().fromJson(json, Post.class);
        JsonObject author = json.getAsJsonObject().get("author").getAsJsonObject();
        post.setAuthorId(author.get("id").getAsInt());
        post.setAuthorName(author.get("fullname").getAsString());
        return post;
    }
}
