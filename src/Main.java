import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import java.util.*;

import controller.Controler;
import com.formdev.flatlaf.FlatLightLaf;
import gui.Login;
import model.*;

public class Main {


    public static void main(String[] args) {
        FlatLightLaf.setup();
        Login jdl = new Login();
        jdl.setSize(700,500);
        Controler c = new Controler(jdl);
        jdl.setControler(c);
        jdl.setLocationRelativeTo(null);
        jdl.setVisible(true);
    }


}