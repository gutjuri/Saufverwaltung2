import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Dialog zum Hinzufügen eines Mitgliedes in die Db.
 * 
 * @author Juri Dispan
 *
 */
public class AddMemberWindow extends Stage {

	public AddMemberWindow(DbConnection dbcon, RefreshingTable tab) {
		GridPane mainBox = new GridPane();
		Scene sc = new Scene(mainBox, 350, 200);
		sc.getStylesheets().add((getClass().getResource("application.css").toString()));

		mainBox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
		mainBox.setPadding(new Insets(25, 25, 25, 25));
		mainBox.setAlignment(Pos.CENTER);
		mainBox.setHgap(10);
		mainBox.setVgap(10);
		mainBox.setMaxWidth(100);
		Text title = new Text("Mitglied hinzufügen");
		title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		mainBox.add(title, 0, 0, 2, 1);

		HBox h1 = new HBox();

		Label nameLabel = new Label("Name:");
		TextField nameFeld = new TextField("");
		Label startGLabel = new Label("Startguthaben:");
		TextField startGFeld = new TextField("0");

		mainBox.add(nameLabel, 0, 1);
		mainBox.add(nameFeld, 1, 1);
		mainBox.add(startGLabel, 0, 2);
		mainBox.add(startGFeld, 1, 2);

		Button fertig = new Button("Fertig");
		fertig.setPrefSize(110, 20);
		fertig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String gtext = startGFeld.getText().replaceAll(",", ".");
				try {

					String name = nameFeld.getText().trim();
					if (name.length() > 8 || name.length() < 2 || dbcon.doesMemberExist(name)) {
						throw new IllegalArgumentException();
					}
					double startGh = Double.parseDouble(gtext);
					dbcon.createMember(name, startGh);
					tab.refreshFull();
					close();

				} catch (NumberFormatException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Fehler");
					alert.setHeaderText("\"" + gtext + "\" ist keine valide Zahl.");
					alert.setContentText("Bitte eine valide Zahl eingeben.");
					alert.showAndWait();
				} catch (IllegalArgumentException e) {
					Alert nameAlert = new Alert(AlertType.ERROR);
					nameAlert.setTitle("Fehler");
					nameAlert.setHeaderText("Name nicht korrekt");
					nameAlert.setContentText("Bitte einen Namen mit 2 < länge < 8 eingeben.\n"
							+ "Es darf kein Mitglied mit dem selben Namen existieren.");
					nameAlert.showAndWait();
				}

			}
		});
		fertig.setDefaultButton(true);
		Button cancel = new Button("Abbrechen");
		cancel.setPrefSize(110, 20);
		cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				close();
			}
		});

		fertig.getStyleClass().add("addButton");
		h1.setAlignment(Pos.BOTTOM_CENTER);
		h1.getChildren().addAll(fertig, cancel);
		h1.setSpacing(10);
		mainBox.add(h1, 0, 3);

		this.setTitle("Abbuchen");
		this.setScene(sc);
		this.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		this.show();

	}

}
