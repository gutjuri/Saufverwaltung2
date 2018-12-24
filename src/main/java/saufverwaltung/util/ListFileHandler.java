package saufverwaltung.util;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ListFileHandler {
    private final String path;
    private final Localizer msg;

    private final List<Integer> colWidths = Arrays.asList(9, 27, 27, 8);
    private final String lineSeparator = times("-", colWidths.get(0)) + "+"
                    + times("-", colWidths.get(1) + 1) + "+" + times("-", colWidths.get(2) + 1)
                    + "+" + times("-", colWidths.get(3));


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
            w.write(padRight(listname,
                            colWidths.stream().reduce(0, (a, b) -> a + b) - dateString.length() + 5)
                            + dateString);
            w.newLine();
            w.newLine();
            w.write(padRight(msg.getString("name") + ":", colWidths.get(0)) + "| "
                            + padRight(boozename + " (1,50\u20ac)", colWidths.get(1)) + "| "
                            + padRight(nbname + " (1\u20ac)", colWidths.get(2)) + "| "
                            + padRight(msg.getString("cap"), colWidths.get(3)));
            w.newLine();
            w.write(lineSeparator);
            w.newLine();
            for (Member cm : memberlist) {
                if (!cm.isVisible()) {
                    continue;
                }

                String blockedStr =
                                padRight(cm.getGuthaben() < 0 ? blocked : " ", colWidths.get(1));
                w.write(padRight(cm.getName(), colWidths.get(0)) + "| " + blockedStr + "| "
                                + blockedStr + "|"
                                + padLeft(cm.getGuthabenFormatted() + "\u20ac", colWidths.get(3)));

                w.newLine();
                w.write(lineSeparator);
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

    private String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    private static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    private String times(String s, int n) {
        StringBuilder sb = new StringBuilder();

        while (n-- > 0) {
            sb.append(s);
        }

        return sb.toString();
    }
}
