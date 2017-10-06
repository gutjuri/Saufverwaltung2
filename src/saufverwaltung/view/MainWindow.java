package saufverwaltung.view;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import saufverwaltung.control.Main;
import saufverwaltung.util.DbConnection;
import saufverwaltung.util.Member;
import saufverwaltung.util.RefreshingTable;

public class MainWindow extends Stage {
	Controller ctl;
	DbConnection dbcon;
	Image icon;

	public MainWindow(Stage primaryStage, DbConnection dbcon, Controller ctl) {
		this.dbcon = dbcon;
		this.ctl = ctl;
		BorderPane bpane = new BorderPane();
		Scene sc = new Scene(bpane, 1000, 600);
		System.out.println(getClass().getResource("application.css"));
		sc.getStylesheets().add((getClass().getResource("application.css").toString()));
		icon = new Image(getClass().getResourceAsStream("res/icon.png"));
		RefreshingTable mid = addTableView();
		VBox vbox = addVbox(primaryStage, mid);
		bpane.setLeft(vbox);
		bpane.setCenter(mid);

		primaryStage.setTitle(Main.msg.getString("appname"));
		primaryStage.setOnCloseRequest(ctl::updateListFile);
		primaryStage.getIcons().add(icon);
		primaryStage.setScene(sc);
		primaryStage.show();
	}

	RefreshingTable addTableView() {

		RefreshingTable retTabView = new RefreshingTable(dbcon);
		TableColumn<Member, String> colName = new TableColumn<>(Main.msg.getString("name"));
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<Member, String> colGuth = new TableColumn<>(Main.msg.getString("balance"));
		colGuth.setCellValueFactory(new PropertyValueFactory<>("guthaben"));
		TableColumn<Member, String> colAlk = new TableColumn<>(Main.msg.getString("booze"));
		colAlk.setCellValueFactory(new PropertyValueFactory<>("alk"));
		TableColumn<Member, String> colAntalk = new TableColumn<>(Main.msg.getString("nonalcoholics"));
		colAntalk.setCellValueFactory(new PropertyValueFactory<>("antalk"));
		TableColumn<Member, String> colVisible = new TableColumn<>(Main.msg.getString("visible"));
		colVisible.setCellValueFactory(new PropertyValueFactory<>("visible"));

		TableColumn colEinzahlButton = new TableColumn(Main.msg.getString("deposit"));
		colEinzahlButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		TableColumn colTrinkButton = new TableColumn(Main.msg.getString("withdraw"));
		colTrinkButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		TableColumn colVisButton = new TableColumn(Main.msg.getString("chvis"));
		colVisButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		Callback<TableColumn<Member, String>, TableCell<Member, String>> cellFactory = new Callback<TableColumn<Member, String>, TableCell<Member, String>>() {
			@Override
			public TableCell call(final TableColumn<Member, String> param) {
				final TableCell<Member, String> cell = new TableCell<Member, String>() {

					final Button btn = new Button(Main.msg.getString("deposit"));

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Member person = getTableView().getItems().get(getIndex());
								new DepositWindow(person, dbcon, retTabView, ctl);
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		Callback<TableColumn<Member, String>, TableCell<Member, String>> cellFactory2 = new Callback<TableColumn<Member, String>, TableCell<Member, String>>() {
			@Override
			public TableCell call(final TableColumn<Member, String> param) {
				final TableCell<Member, String> cell = new TableCell<Member, String>() {

					final Button btn = new Button(Main.msg.getString("withdraw"));

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Member person = getTableView().getItems().get(getIndex());
								new WithdrawWindow(person, dbcon, retTabView, ctl);

							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		Callback<TableColumn<Member, String>, TableCell<Member, String>> cellFactoryVis = new Callback<TableColumn<Member, String>, TableCell<Member, String>>() {
			@Override
			public TableCell call(final TableColumn<Member, String> param) {
				final TableCell<Member, String> cell = new TableCell<Member, String>() {

					final Button btn = new Button(Main.msg.getString("chvis"));

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Member person = getTableView().getItems().get(getIndex());
								person.toggleVisible();
								dbcon.toggleVisible(person.getName());
								retTabView.refresh();

							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		colEinzahlButton.setCellFactory(cellFactory);
		colTrinkButton.setCellFactory(cellFactory2);
		colVisButton.setCellFactory(cellFactoryVis);
		retTabView.getColumns().addAll(colName, colGuth, colAlk, colAntalk, colVisible, colEinzahlButton,
				colTrinkButton, colVisButton);
		return retTabView;
	}

	VBox addVbox(Stage primaryStage, RefreshingTable tab) {
		VBox vbox = new VBox();

		vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
		vbox.setPadding(new Insets(15, 12, 15, 12));
		vbox.setSpacing(10);

		Button saveexit = new Button(Main.msg.getString("close"));
		saveexit.setPrefSize(150, 20);
		saveexit.setOnAction(e -> ctl.updateListFileAndClose(e, primaryStage));

		Button impr = new Button(Main.msg.getString("impr"));
		impr.setPrefSize(150, 20);
		impr.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new InfoWindow(ctl);
			}
		});

		Button del = new Button(Main.msg.getString("delmember"));
		del.setPrefSize(150, 20);
		del.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new DeleteWindow(dbcon, tab, ctl);
			}
		});

		Button addMember = new Button(Main.msg.getString("addmember"));
		addMember.setPrefSize(150, 20);
		addMember.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new AddMemberWindow(dbcon, tab, ctl);
			}
		});

		Button renameMember = new Button(Main.msg.getString("rename"));
		renameMember.setPrefSize(150, 20);
		renameMember.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new RenameWindow(dbcon, tab, ctl);
			}
		});

		ComboBox<String> changelang = new ComboBox<>(FXCollections.observableArrayList("Deutsch", "English"));
		changelang.setPromptText(Main.msg.getString("lang"));
		changelang.setPrefSize(150, 20);
		changelang.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				switch (changelang.getValue()) {
				case "Deutsch":
					Main.lang = "de";
					Main.country = "DE";
					break;
				case "English":
					Main.lang = "en";
					Main.country = "UK";
					break;
				}
				Locale loc = new Locale(Main.lang, Main.country);
				Main.msg = ResourceBundle.getBundle("MsgBundle", loc);

			}

		});

		Button open = new Button(Main.msg.getString("openlist"));
		open.setPrefSize(150, 20);
		open.setOnAction(ctl::openListFile);

		vbox.getChildren().addAll(addMember, del, renameMember, impr, open, saveexit);
		return vbox;
	}
}
