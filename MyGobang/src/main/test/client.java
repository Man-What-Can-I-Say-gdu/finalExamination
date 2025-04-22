import com.pumpkin.entity.User;
import org.junit.jupiter.api.Test;

public class client {
   @Test
    void testClient(){
       User user = new User();
       GobangClientEndpoint gobangClientEndpoint = new GobangClientEndpoint(user);
       gobangClientEndpoint.onStringMessage("joinInNotice");
    }
}
