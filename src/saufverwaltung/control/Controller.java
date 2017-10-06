package saufverwaltung.control;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import saufverwaltung.util.DbConnection;
import saufverwaltung.util.Member;
import saufverwaltung.util.RefreshingTable;

public class Controller {
	ResourceBundle msg;
	DbConnection dbcon;
	String listpath;

	public Controller(ResourceBundle msg, DbConnection dbcon, String listpath) {
		this.msg = msg;
		this.dbcon = dbcon;
		this.listpath = listpath;
	}

	public void updateMemberStats(Member member, Stage stage, TextField alkanz, TextField antalkanz,
			TableView<Member> retTabView) {
		try {
			int _alk = Integer.parseInt(alkanz.getText());
			int _aalk = Integer.parseInt(antalkanz.getText());
			dbcon.updateStats(member.getName(), _alk, _aalk);
			member.setAlk(member.getAlk() + _alk);
			member.setAntalk(member.getAntalk() + _aalk);
			member.setGuthaben(member.getGuthaben() - _aalk - (1.5 * _alk));
			retTabView.refresh();
			stage.close();
		} catch (NumberFormatException e) {
			// TODO Error
		}
	}

	public void updateListFileAndClose(ActionEvent e, Stage primaryStage) {
		updateListFile(e);
		primaryStage.close();
	}

	private void updateListFile() {
		ObservableList<Member> obslist = null;
		try {
			obslist = FXCollections.observableArrayList(dbcon.getMembers());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String path = "";
		Date date = Calendar.getInstance().getTime(); // Date
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
		String dateString = dateFormatter.format(date);
		try {
			path = "/data/strichliste.txt";
			BufferedWriter w = new BufferedWriter(new FileWriter(path));
			String listname = msg.getString("list");
			String boozename = msg.getString("booze");
			String nbname = msg.getString("nonalcoholics");
			String blocked = msg.getString("blocked");
			w.write(listname + ":\t \t \t \t \t \t \t   " + dateString);
			w.newLine();
			w.newLine();
			w.write(msg.getString("name") + ": \t | " + boozename + " (1,50\u20ac)       |     " + nbname
					+ " (1\u20ac) | " + msg.getString("cap"));
			w.newLine();
			w.write("---------+-----------------------------+----------------------------+--------");
			w.newLine();
			for (Member cm : obslist) {
				if (!cm.isVisible()) {
					continue;
				}
				if (cm.getGuthaben() < 0) {
					w.write(cm.getName() + "\t | " + blocked + " \t \t       | " + blocked + "\t\t    | "
							+ cm.getGuthaben());
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

	public void updateListFile(WindowEvent e) {
		updateListFile();
	}

	public void updateListFile(ActionEvent e) {
		updateListFile();
	}

	public void openListFile(ActionEvent e) {
		if (!Desktop.isDesktopSupported())
			throw new OutOfMemoryError();
		Desktop desktop = Desktop.getDesktop();
		File file = new File(listpath);
		try {
			desktop.open(file);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	public void handleAddMember(Stage stage, TextField depositField, TextField nameField, RefreshingTable tab) {
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
			alert.setTitle(Main.msg.getString("error"));
			alert.setHeaderText("\"" + gtext + "\"" + Main.msg.getString("isnonum") + ".");
			alert.setContentText(Main.msg.getString("plsnum") + ".");
			alert.showAndWait();
		} catch (IllegalArgumentException e) {
			Alert nameAlert = new Alert(AlertType.ERROR);
			nameAlert.setTitle(Main.msg.getString("error"));
			nameAlert.setHeaderText(Main.msg.getString("noname"));
			nameAlert.setContentText(Main.msg.getString("plsname") + ".");
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
			alert.setTitle(Main.msg.getString("notfound"));
			alert.setHeaderText(Main.msg.getString("error"));
			alert.setContentText(Main.msg.getString("notfoundtext").replace("{}", name) + ".");
			alert.showAndWait();
			stage.close();
		}
	}

	public void handleRenameMember(Stage stage, ComboBox<Member> cbox, TextField tfield, TableView<Member> retTabView) {
		Member cmember = cbox.getValue();
		String oldname = cmember.getName();
		String newname = tfield.getText();
		dbcon.renameUser(oldname, newname);
		cmember.setName(newname);
		retTabView.refresh();
		stage.close();
	}

	public void handleDeposit(Stage stage, TextField tf, Member member, TableView<Member> tab) {
		String str = tf.getText().replaceAll(",", ".");
		try {
			double betr = Double.parseDouble(str);
			dbcon.deposit(member.getName(), betr);
			member.setGuthaben(member.getGuthaben() + betr);
			tab.refresh();
			stage.close();
		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(Main.msg.getString("error"));
			alert.setHeaderText("\"" + str + "\" " + Main.msg.getString("isnonum") + ".");
			alert.setContentText(Main.msg.getString("plsnum") + ".");
			alert.showAndWait();
		}
	}

	public void handleInfoStateChange(int i, Label label) {
		if (i % 2 == 0) {
			label.setText(Main.msg.getString("onbugs") + " -> juri.dispan@me.com");
		} else {
			label.setText("by Juri Dispan 2017");
		}
	}
}
