package saufverwaltung.util;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class RefreshingTable extends TableView<Member> {
	DbConnection dbcon;
	ObservableList<Member> obslist = null;

	public RefreshingTable(DbConnection dbcon) {
		super();
		this.dbcon = dbcon;
		refreshObsList();
		this.setItems(obslist);
	}

	public void refreshFull() {
		refreshObsList();
		this.setItems(obslist);
		super.refresh();
	}

	private void refreshObsList() {
		try {
			obslist = FXCollections.observableArrayList(dbcon.getMembers());
		} catch (SQLException e) {
			dbcon.makeNewMembersTable();
			try {
				obslist = FXCollections.observableArrayList(dbcon.getMembers());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
