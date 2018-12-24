package saufverwaltung.control;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.stage.Stage;
import saufverwaltung.util.DbConnection;
import saufverwaltung.util.DbConnection_Old;
import saufverwaltung.util.HSQLDBConnection;
import saufverwaltung.util.Localizer;
import saufverwaltung.util.Member;
import saufverwaltung.view.MainWindow;

/**
 * Hauptfenster mit all dem fancy Zeug (v.a. die große tabelle in der mitte). �bernimmt auch das
 * Erstellen des Strichlistenfiles.
 * 
 * @author Juri Dispan
 */

public class Main extends Application {

    private DbConnection dbcon = null;
    public static String lang = "de";
    public static String country = "DE";
    static Controller ctl;
    private Localizer localizer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        List<String> args = getParameters().getRaw();
        if (args.size() == 2) {
            lang = args.get(0);
            country = args.get(1);
        }
        Locale loc = new Locale(lang, country);
        localizer = new Localizer(ResourceBundle.getBundle("MsgBundle", loc));

        dbcon = initDatabase();


        ctl = new Controller(localizer, dbcon);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new MainWindow(primaryStage, dbcon, ctl, localizer);
    }

    private DbConnection initDatabase() {
        DbConnection dbcon = new HSQLDBConnection();

        if (!dbcon.doesTableExist()) {
            DbConnection_Old old = new DbConnection_Old();
            dbcon.createEmptyTable();

            if (old.doesTableExist()) {
                List<Member> members = old.getMembers();

                members.forEach(member -> {
                    dbcon.createMember(member.getName(), member.getGuthaben());
                    dbcon.setStats(member.getName(), member.getAlk(), member.getAntalk());
                    if (!member.isVisible()) {
                        dbcon.toggleVisible(member.getName());
                    }
                });
            }
        }

        return dbcon;
    }
}
