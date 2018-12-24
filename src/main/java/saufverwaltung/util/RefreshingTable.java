package saufverwaltung.util;

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
        if (!dbcon.doesTableExist()) {
            dbcon.createEmptyTable();
        }

        obslist = FXCollections.observableArrayList(dbcon.getMembers());
    }
}
