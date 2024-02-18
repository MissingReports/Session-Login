package missing.mods.sessionlogin.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class AuthUtils {

    public static String[] getTokenInfo(String accessToken) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
            request.setHeader("Authorization", "Bearer " + accessToken);
            CloseableHttpResponse response = client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
            String IGN = jsonObject.get("name").getAsString();
            String UUID = jsonObject.get("id").getAsString();
            return new String[]{IGN, UUID};
        } catch (Exception e) {
            return null;
        }
    }

    public static String[] authenticate(String accessToken) {
        String[] info = getTokenInfo(accessToken);
        if (info == null) {
            return null;
        }
        try {
            String name = info[0];
            String uuid = info[1];
            Session session = new Session(name, uuid, accessToken, null, null, Session.AccountType.MSA);
            MinecraftClient client = MinecraftClient.getInstance();
            Field field = MinecraftClient.class.getDeclaredField("session");
            field.setAccessible(true);
            field.set(client, session);
            return info;
        } catch (Exception e) {
            return null;
        }
    }
}
