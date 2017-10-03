import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * Hauptfenster mit all dem fancy Zeug (v.a. die große tabelle in der mitte).
 * Übernimmt auch das Erstellen des Strichlistenfiles.
 * 
 * @author Juri Dispan
 */

public class Main extends Application {

	static DbConnection dbcon = null;
	static Image icon;

	public static void main(String[] args) {
		dbcon = new DbConnection();
		// dbcon.testPath();
		// dbcon.createMember("Juri");
		// dbcon.printMembers();

		launch(args);
		// dbcon.printMembers();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane bpane = new BorderPane();
		// System.out.println(getClass().getResource("/icon.png"));
		icon = new Image(getClass().getResourceAsStream("/icon.png"));
		TableView<Member> mid = addTableView();
		VBox vbox = addVbox(primaryStage, mid);
		bpane.setLeft(vbox);
		bpane.setCenter(mid);

		// StackPane root = new StackPane();
		// root.getChildren().add(bpane);
		Scene sc = new Scene(bpane, 1000, 600);
		primaryStage.setTitle("Saufverwaltung V2");
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				makefile();
			}

		});
		primaryStage.getIcons().add(icon);
		primaryStage.setScene(sc);
		primaryStage.show();
	}

	TableView<Member> addTableView() {

		TableView<Member> retTabView = new RefreshingTable(dbcon);
		TableColumn<Member, String> colName = new TableColumn<>("Name");
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<Member, String> colGuth = new TableColumn<>("Guthaben");
		colGuth.setCellValueFactory(new PropertyValueFactory<>("guthaben"));
		TableColumn<Member, String> colAlk = new TableColumn<>("Alkoholisches");
		colAlk.setCellValueFactory(new PropertyValueFactory<>("alk"));
		TableColumn<Member, String> colAntalk = new TableColumn<>("Antialkoholisches");
		colAntalk.setCellValueFactory(new PropertyValueFactory<>("antalk"));
		TableColumn<Member, String> colVisible = new TableColumn<>("Sichtbar");
		colVisible.setCellValueFactory(new PropertyValueFactory<>("visible"));

		TableColumn colEinzahlButton = new TableColumn("Einzahlen");
		colEinzahlButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		TableColumn colTrinkButton = new TableColumn("Abbuchen");
		colTrinkButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		TableColumn colVisButton = new TableColumn("Sichtbarkeit");
		colVisButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		Callback<TableColumn<Member, String>, TableCell<Member, String>> cellFactory = new Callback<TableColumn<Member, String>, TableCell<Member, String>>() {
			@Override
			public TableCell call(final TableColumn<Member, String> param) {
				final TableCell<Member, String> cell = new TableCell<Member, String>() {

					final Button btn = new Button("Einzahlen");

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Member person = getTableView().getItems().get(getIndex());
								new EinzahlWindow(person, dbcon, retTabView);

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

					final Button btn = new Button("Abbuchen");

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Member person = getTableView().getItems().get(getIndex());
								new AbbuchWindow(person, dbcon, retTabView);

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

					final Button btn = new Button("Sichtbarkeit ändern");

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

	VBox addVbox(Stage primaryStage, TableView<Member> tab) {
		VBox vbox = new VBox();

		vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
		vbox.setPadding(new Insets(15, 12, 15, 12));
		vbox.setSpacing(10);

		Button saveexit = new Button("Beenden");
		saveexit.setPrefSize(150, 20);
		saveexit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				makefile();
				primaryStage.close();
			}
		});

		Button impr = new Button("Impressum");
		impr.setPrefSize(150, 20);
		impr.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new Impressum();
			}
		});

		Button del = new Button("Mitglied löschen");
		del.setPrefSize(150, 20);
		del.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new DeleteWindow(dbcon, tab);
			}
		});

		Button addMember = new Button("Mitglied hinzufügen");
		addMember.setPrefSize(150, 20);
		addMember.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new AddMemberWindow(dbcon, tab);
			}
		});

		Button open = new Button("Liste öffnen");
		open.setPrefSize(150, 20);
		open.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!Desktop.isDesktopSupported())
					throw new OutOfMemoryError();
				Desktop desktop = Desktop.getDesktop();

				String path = makefile();
				File file = new File(path);
				try {
					desktop.open(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		vbox.getChildren().addAll(addMember, del, impr, open, saveexit);
		return vbox;
	}

	public String makefile() {
		ObservableList<Member> obslist = null;
		try {
			obslist = FXCollections.observableArrayList(dbcon.getMembers());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String path = "";
		Date date = Calendar.getInstance().getTime(); // Datum
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
		String dateString = dateFormatter.format(date);
		try {
			URI tmp = null;
			try {
				tmp = getClass().getResource("/icon.png").toURI();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// path = tmp.toString().replace("icon.png", "").replace("jar:",
			// "");
			// Path pa =
			// Paths.get(tmp.toString().replace("Saufverwaltung2.jar!",
			// "").replace("/icon.png", "")
			// .replace("jar:", "").replace("file:", "").replace("/C:", ""));
			// path = pa.getRoot().toAbsolutePath().toString().replace("\\", "")
			// + pa.toString().replace("\\", "/")
			// + "/data/strichliste.txt";
			// String path = pa.toString()+"/data/strichliste.txt");
			path = "/data/strichliste.txt";
			// System.out.println(path);
			BufferedWriter w = new BufferedWriter(new FileWriter(path));
			w.write("Strichliste:\t \t \t \t \t \t \t   " + dateString);
			w.newLine();
			w.newLine();
			w.write("Name: \t | Alkoholisches (1,50\u20ac)       |     Antialkoholisches (1\u20ac) | Kapital");
			w.newLine();
			w.write("---------+-----------------------------+----------------------------+--------");
			w.newLine();
			for (Member cm : obslist) {
				if (!cm.isVisible()) {
					continue;
				}
				if (cm.getGuthaben() < 0) {
					w.write(cm.getName() + "\t | GESPERRT \t \t       | GESPERRT\t\t    | " + cm.getGuthaben());
				} else {
					w.write(cm.getName() + "\t | \t \t \t       | \t\t\t    | " + cm.getGuthaben());
				}

				w.newLine();
				w.write("---------+-----------------------------+----------------------------+--------");
				w.newLine();

			}
			w.flush();
			w.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return path;

	}

}
