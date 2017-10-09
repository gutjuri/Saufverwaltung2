package saufverwaltung.control;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.stage.Stage;
import saufverwaltung.util.DbConnection;
import saufverwaltung.view.MainWindow;

/**
 * Hauptfenster mit all dem fancy Zeug (v.a. die groﬂe tabelle in der mitte).
 * ‹bernimmt auch das Erstellen des Strichlistenfiles.
 * 
 * @author Juri Dispan
 */

public class Main extends Application {

	static DbConnection dbcon = null;
	public static String lang = "de";
	public static String country = "DE";
	public static ResourceBundle msg;
	static Controller ctl;

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
		msg = ResourceBundle.getBundle("MsgBundle", loc);
		dbcon = new DbConnection();
		String listpath = "/data/strichliste.txt";
		ctl = new Controller(msg, dbcon, listpath);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new MainWindow(primaryStage, dbcon, ctl);
	}

}
