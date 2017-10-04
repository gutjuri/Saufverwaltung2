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
import java.util.Locale;
import java.util.ResourceBundle;

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
 * Hauptfenster mit all dem fancy Zeug (v.a. die groﬂe tabelle in der mitte).
 * ‹bernimmt auch das Erstellen des Strichlistenfiles.
 * 
 * @author Juri Dispan
 */

public class Main extends Application {

	static DbConnection dbcon = null;
	static Image icon;
	static String lang = "de";
	static String country = "DE";
	public static ResourceBundle msg;

	public static void main(String[] args) {
		if (args.length == 2) {
			lang = args[0];
			country = args[1];
		}
		Locale loc = new Locale(lang, country);
		msg = ResourceBundle.getBundle("MsgBundle", loc);
		dbcon = new DbConnection();
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane bpane = new BorderPane();
		Scene sc = new Scene(bpane, 1000, 600);
		sc.getStylesheets().add((getClass().getResource("application.css").toString()));
		icon = new Image(getClass().getResourceAsStream("icon.png"));
		RefreshingTable mid = addTableView();
		VBox vbox = addVbox(primaryStage, mid);
		bpane.setLeft(vbox);
		bpane.setCenter(mid);

		primaryStage.setTitle(msg.getString("appname"));
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

	RefreshingTable addTableView() {

		RefreshingTable retTabView = new RefreshingTable(dbcon);
		TableColumn<Member, String> colName = new TableColumn<>(msg.getString("name"));
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<Member, String> colGuth = new TableColumn<>(msg.getString("balance"));
		colGuth.setCellValueFactory(new PropertyValueFactory<>("guthaben"));
		TableColumn<Member, String> colAlk = new TableColumn<>(msg.getString("booze"));
		colAlk.setCellValueFactory(new PropertyValueFactory<>("alk"));
		TableColumn<Member, String> colAntalk = new TableColumn<>(msg.getString("nonalcoholics"));
		colAntalk.setCellValueFactory(new PropertyValueFactory<>("antalk"));
		TableColumn<Member, String> colVisible = new TableColumn<>(msg.getString("visible"));
		colVisible.setCellValueFactory(new PropertyValueFactory<>("visible"));

		TableColumn colEinzahlButton = new TableColumn(msg.getString("deposit"));
		colEinzahlButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		TableColumn colTrinkButton = new TableColumn(msg.getString("withdraw"));
		colTrinkButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		TableColumn colVisButton = new TableColumn(msg.getString("chvis"));
		colVisButton.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		Callback<TableColumn<Member, String>, TableCell<Member, String>> cellFactory = new Callback<TableColumn<Member, String>, TableCell<Member, String>>() {
			@Override
			public TableCell call(final TableColumn<Member, String> param) {
				final TableCell<Member, String> cell = new TableCell<Member, String>() {

					final Button btn = new Button(msg.getString("deposit"));

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

					final Button btn = new Button(msg.getString("withdraw"));

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

					final Button btn = new Button(msg.getString("chvis"));

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

		Button saveexit = new Button(msg.getString("close"));
		saveexit.setPrefSize(150, 20);
		saveexit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				makefile();
				primaryStage.close();
			}
		});

		Button impr = new Button(msg.getString("impr"));
		impr.setPrefSize(150, 20);
		impr.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new Impressum();
			}
		});

		Button del = new Button(msg.getString("delmember"));
		del.setPrefSize(150, 20);
		del.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new DeleteWindow(dbcon, tab);
			}
		});

		Button addMember = new Button(msg.getString("addmember"));
		addMember.setPrefSize(150, 20);
		addMember.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new AddMemberWindow(dbcon, tab);
			}
		});

		Button renameMember = new Button(msg.getString("rename"));
		renameMember.setPrefSize(150, 20);
		renameMember.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new RenameWindow(dbcon, tab);
			}
		});

		Button open = new Button(msg.getString("openlist"));
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

		vbox.getChildren().addAll(addMember, del, renameMember, impr, open, saveexit);
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
			path = "/data/strichliste.txt";
			// System.out.println(path);
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
		} catch (IOException e) {

			e.printStackTrace();
		}
		return path;

	}

}
