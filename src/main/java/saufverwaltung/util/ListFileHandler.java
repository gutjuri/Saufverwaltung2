package saufverwaltung.util;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ListFileHandler {
    private final String path;
    private final Localizer msg;

    public ListFileHandler(String path, Localizer msg) {
        this.path = path;
        this.msg = msg;
    }

    public void writeListToFile(List<Member> memberlist) {
        Date date = Calendar.getInstance().getTime(); // Date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormatter.format(date);
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(path));
            String listname = msg.getString("list");
            String boozename = msg.getString("booze");
            String nbname = msg.getString("nonalcoholics");
            String blocked = msg.getString("blocked");
            w.write(listname + ":\t \t \t \t \t \t \t   " + dateString);
            w.newLine();
            w.newLine();
            w.write(msg.getString("name") + ": \t | " + boozename + " (1,50\u20ac)       |     "
                            + nbname + " (1\u20ac) | " + msg.getString("cap"));
            w.newLine();
            w.write("---------+-----------------------------+----------------------------+--------");
            w.newLine();
            for (Member cm : memberlist) {
                if (!cm.isVisible()) {
                    continue;
                }
                if (cm.getGuthabenNumeric() < 0) {
                    w.write(cm.getName() + "\t | " + blocked + " \t \t       | " + blocked
                                    + "\t\t    | " + cm.getGuthaben());
                } else {
                    w.write(cm.getName() + "\t | \t \t \t       | \t\t\t    | " + cm.getGuthaben());
                }

                w.newLine();
                w.write("---------+-----------------------------+----------------------------+--------");
                w.newLine();

            }
            w.flush();
            w.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void openFile() {
        if (!Desktop.isDesktopSupported()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(msg.getString("error"));
            alert.setContentText(msg.getString("notsup") + ".");
            alert.showAndWait();
        }
        Desktop desktop = Desktop.getDesktop();
        File file = new File(path);
        try {
            desktop.open(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
