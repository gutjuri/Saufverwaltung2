package saufverwaltung.control;

import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import saufverwaltung.util.DbConnection_Old;
import saufverwaltung.util.ListFileHandler;
import saufverwaltung.util.Localizer;
import saufverwaltung.util.Member;
import saufverwaltung.util.RefreshingTable;

public class Controller {
    private Localizer localizer;
    private DbConnection_Old dbcon;
    private ListFileHandler fileHandler;

    public Controller(Localizer msg, DbConnection_Old dbcon) {
        this.localizer = msg;
        this.dbcon = dbcon;
        fileHandler = new ListFileHandler("data/strichliste.txt", msg);
    }

    public void updateMemberStats(Member member, Stage stage, TextField alkanz, TextField antalkanz,
                    TableView<Member> retTabView) {
        try {
            int alk = Integer.parseInt(alkanz.getText());
            int aalk = Integer.parseInt(antalkanz.getText());
            dbcon.updateStats(member.getName(), alk, aalk);
            member.setAlk(member.getAlk() + alk);
            member.setAntalk(member.getAntalk() + aalk);
            member.setGuthaben(member.getGuthabenNumeric() - aalk - (1.5 * alk));
            retTabView.refresh();
            stage.close();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(localizer.getString("error"));
            alert.setHeaderText(localizer.getString("error"));
            alert.setContentText(localizer.getString("wholenumpls") + ".");
            alert.showAndWait();
        }
    }

    public void updateListFileAndClose(ActionEvent e, Stage primaryStage) {
        updateListFile(e);
        primaryStage.close();
    }

    private void updateListFile() {
        try {
            fileHandler.writeListToFile(dbcon.getMembers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateListFile(WindowEvent e) {
        updateListFile();
    }

    public void updateListFile(ActionEvent e) {
        updateListFile();
    }

    public void openListFile(ActionEvent e) {
        updateListFile();
        fileHandler.openFile();
    }

    public void handleAddMember(Stage stage, TextField depositField, TextField nameField,
                    RefreshingTable tab) {
        String gtext = depositField.getText().replaceAll(",", ".");
        try {

            String name = nameField.getText().trim();
            if (name.length() > 8 || name.length() < 2 || dbcon.doesMemberExist(name)) {
                throw new IllegalArgumentException();
            }
            double startGh = Double.parseDouble(gtext);
            dbcon.createMember(name, startGh);
            tab.refreshFull();
            stage.close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(localizer.getString("error"));
            alert.setHeaderText("\"" + gtext + "\"" + localizer.getString("isnonum") + ".");
            alert.setContentText(localizer.getString("plsnum") + ".");
            alert.showAndWait();
        } catch (IllegalArgumentException e) {
            Alert nameAlert = new Alert(AlertType.ERROR);
            nameAlert.setTitle(localizer.getString("error"));
            nameAlert.setHeaderText(localizer.getString("noname"));
            nameAlert.setContentText(localizer.getString("plsname") + ".");
            nameAlert.showAndWait();
        }

    }

    public void handleDeleteMember(Stage stage, TextField tf, RefreshingTable tab) {
        String name = tf.getText().trim();
        try {
            if (!dbcon.doesMemberExist(name)) {
                throw new SQLException();
            }
            dbcon.deleteMember(name);
            tab.refreshFull();
            stage.close();
        } catch (SQLException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(localizer.getString("notfound"));
            alert.setHeaderText(localizer.getString("error"));
            alert.setContentText(localizer.getString("notfoundtext").replace("{}", name) + ".");
            alert.showAndWait();
            stage.close();
        }
    }

    public void handleRenameMember(Stage stage, ComboBox<Member> cbox, TextField tfield,
                    TableView<Member> retTabView) {
        Member cmember = cbox.getValue();
        String oldname = cmember.getName();
        String newname = tfield.getText();
        dbcon.renameMember(oldname, newname);
        cmember.setName(newname);
        retTabView.refresh();
        stage.close();
    }

    public void handleDeposit(Stage stage, TextField tf, Member member, TableView<Member> tab) {
        String str = tf.getText().replaceAll(",", ".");
        try {
            double betr = Double.parseDouble(str);
            dbcon.deposit(member.getName(), betr);
            member.setGuthaben(member.getGuthabenNumeric() + betr);
            tab.refresh();
            stage.close();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(localizer.getString("error"));
            alert.setHeaderText("\"" + str + "\" " + localizer.getString("isnonum") + ".");
            alert.setContentText(localizer.getString("plsnum") + ".");
            alert.showAndWait();
        }
    }

    public void handleInfoStateChange(int i, Label label) {
        if (i % 2 == 0) {
            label.setText(localizer.getString("onbugs") + " -> juri.dispan@me.com");
        } else {
            label.setText("by Juri Dispan 2017");
        }
    }
}
