package ServerTest;

import com.Pumpkin.Controller.GameController;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerTest {
    @Test
    public void TestServer(){

    }

    @Test
    public void TestServer2(){
        GameController gameController = new GameController();
        System.out.println(gameController.startGame());
    }
}
