package saufverwaltung.view;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import saufverwaltung.control.Controller;
import saufverwaltung.util.DbConnection_Old;
import saufverwaltung.util.Localizer;
import saufverwaltung.util.Member;
import saufverwaltung.util.RefreshingTable;

public class MainWindow extends Stage {
    Controller ctl;
    DbConnection_Old dbcon;
    Image icon;
    private final Localizer localizer;

    public MainWindow(Stage primaryStage, DbConnection_Old dbcon, Controller ctl, Localizer localizer) {
        this.dbcon = dbcon;
        this.ctl = ctl;
        this.localizer = localizer;
        BorderPane bpane = new BorderPane();
        Scene sc = new Scene(bpane, 1000, 600);
        sc.getStylesheets().add("application.css");
        icon = new Image("icon.png");
        RefreshingTable mid = addTableView();
        VBox vbox = addVbox(primaryStage, mid);
        bpane.setLeft(vbox);
        bpane.setCenter(mid);

        primaryStage.setTitle(localizer.getString("appname"));
        primaryStage.setOnCloseRequest(ctl::updateListFile);
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(sc);
        primaryStage.show();
    }

    private RefreshingTable addTableView() {
        RefreshingTable retTabView = new RefreshingTable(dbcon);
        List<TableColumn<Member, String>> columns = new ArrayList<>(8);
        TableColumn<Member, String> colName = new TableColumn<>(localizer.getString("name"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columns.add(colName);
        TableColumn<Member, String> colGuth = new TableColumn<>(localizer.getString("balance"));
        colGuth.setCellValueFactory(new PropertyValueFactory<>("guthaben"));
        columns.add(colGuth);
        TableColumn<Member, String> colAlk = new TableColumn<>(localizer.getString("booze"));
        colAlk.setCellValueFactory(new PropertyValueFactory<>("alk"));
        columns.add(colAlk);
        TableColumn<Member, String> colAntalk =
                        new TableColumn<>(localizer.getString("nonalcoholics"));
        colAntalk.setCellValueFactory(new PropertyValueFactory<>("antalk"));
        columns.add(colAntalk);
        TableColumn<Member, String> colVisible = new TableColumn<>(localizer.getString("visible"));
        colVisible.setCellValueFactory(new PropertyValueFactory<>("visible"));
        columns.add(colVisible);

        TableColumn<Member, String> colEinzahlButton =
                        new TableColumn<>(localizer.getString("deposit"));
        colEinzahlButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        columns.add(colEinzahlButton);

        TableColumn<Member, String> colTrinkButton =
                        new TableColumn<>(localizer.getString("withdraw"));
        colTrinkButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        columns.add(colTrinkButton);

        TableColumn<Member, String> colVisButton = new TableColumn<>(localizer.getString("chvis"));
        colVisButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        columns.add(colVisButton);

        Callback<TableColumn<Member, String>, TableCell<Member, String>> cellFactory =
                        getCellFactory(localizer.getString("deposit"),
                                        (item, event) -> new DepositWindow(item, dbcon, retTabView,
                                                        ctl, localizer));


        Callback<TableColumn<Member, String>, TableCell<Member, String>> cellFactory2 =
                        getCellFactory(localizer.getString("withdraw"),
                                        (item, event) -> new WithdrawWindow(item, dbcon, retTabView,
                                                        ctl, localizer));

        Callback<TableColumn<Member, String>, TableCell<Member, String>> cellFactoryVis =
                        getCellFactory(localizer.getString("chvis"), (item, event) -> {
                            item.toggleVisible();
                            dbcon.toggleVisible(item.getName());
                            retTabView.refresh();
                        });

        colEinzahlButton.setCellFactory(cellFactory);
        colTrinkButton.setCellFactory(cellFactory2);
        colVisButton.setCellFactory(cellFactoryVis);
        retTabView.getColumns().addAll(columns);
        return retTabView;
    }

    private VBox addVbox(Stage primaryStage, RefreshingTable tab) {
        VBox vbox = new VBox();

        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);

        Button saveexit = new Button(localizer.getString("close"));
        saveexit.setPrefSize(150, 20);
        saveexit.setOnAction(e -> ctl.updateListFileAndClose(e, primaryStage));

        Button impr = new Button(localizer.getString("impr"));
        impr.setPrefSize(150, 20);
        impr.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                new InfoWindow(ctl, localizer);
            }
        });

        Button del = new Button(localizer.getString("delmember"));
        del.setPrefSize(150, 20);
        del.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                new DeleteWindow(dbcon, tab, ctl, localizer);
            }
        });

        Button addMember = new Button(localizer.getString("addmember"));
        addMember.setPrefSize(150, 20);
        addMember.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                new AddMemberWindow(dbcon, tab, ctl, localizer);
            }
        });

        Button renameMember = new Button(localizer.getString("rename"));
        renameMember.setPrefSize(150, 20);
        renameMember.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                new RenameWindow(dbcon, tab, ctl, localizer);
            }
        });
        /*
         * ComboBox<String> changelang = new ComboBox<>(FXCollections.observableArrayList("Deutsch",
         * "English")); changelang.setPromptText(localizer.getString("lang"));
         * changelang.setPrefSize(150, 20); changelang.setOnAction(new EventHandler<ActionEvent>() {
         * 
         * @Override public void handle(ActionEvent event) { switch (changelang.getValue()) { case
         * "Deutsch": Main.lang = "de"; Main.country = "DE"; break; case "English": Main.lang =
         * "en"; Main.country = "UK"; break; } Locale loc = new Locale(Main.lang, Main.country);
         * localizer = ResourceBundle.getBundle("MsgBundle", loc);
         * 
         * }
         * 
         * });
         */

        Button open = new Button(localizer.getString("openlist"));
        open.setPrefSize(150, 20);
        open.setOnAction(ctl::openListFile);

        vbox.getChildren().addAll(addMember, del, renameMember, impr, open, saveexit);
        return vbox;
    }

    private Callback<TableColumn<Member, String>, TableCell<Member, String>> getCellFactory(
                    String buttonText, OnTableButtonAction<Member> eventHandler) {
        return (param) -> {
            final TableCell<Member, String> cell = new TableCell<Member, String>() {
                final Button btn = new Button(buttonText);

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        btn.setOnAction(e -> eventHandler
                                        .onAction(getTableView().getItems().get(getIndex()), e));
                        setGraphic(btn);
                        setText(null);
                    }
                }
            };
            return cell;

        };
    }

    @FunctionalInterface
    public static interface OnTableButtonAction<T> {
        public void onAction(T item, ActionEvent e);
    }
}
