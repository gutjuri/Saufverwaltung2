package application;

import java.sql.SQLException;

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
 * Dialog zum Löschen eines Mitgliedes aus der Datenbank.
 * 
 * @author Juri Dispan
 *
 */
public class DeleteWindow extends Stage {

	public DeleteWindow(DbConnection dbcon) {
		GridPane mainBox = new GridPane();

		mainBox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
		mainBox.setPadding(new Insets(25, 25, 25, 25));
		mainBox.setAlignment(Pos.CENTER);
		mainBox.setHgap(10);
		mainBox.setVgap(10);
		mainBox.setMaxWidth(100);
		Text title = new Text("Mitglied löschen");
		title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		mainBox.add(title, 0, 0, 2, 1);

		Label betrag = new Label("Welches Mitglied soll gelöscht werden? ");
		mainBox.add(betrag, 0, 1);
		TextField betrFeld = new TextField("");
		mainBox.add(betrFeld, 1, 1);
		betrFeld.setPrefWidth(100);

		Button fertig = new Button("Löschen");
		fertig.setPrefSize(150, 20);
		fertig.setStyle("-fx-base: #ff0000;");
		fertig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				String name = betrFeld.getText().trim();
				try {
					dbcon.deleteMember(name);
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Neustart vornöten");
					alert.setHeaderText("It's not a bug, it's a feature!");
					alert.setContentText(
							"Programm muss neu gestartet werden, damit das Mitglied nicht mehr angezeigt wird.");
					alert.showAndWait();
					close();
				} catch (SQLException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Nicht gefunden");
					alert.setHeaderText("Fehler");
					alert.setContentText("Das Mitglied \"" + name + "\" wurde nicht gefunden.");
					alert.showAndWait();
					close();
				}
			}
		});

		Button cancel = new Button("Abbrechen");
		cancel.setPrefSize(150, 20);
		cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				close();
			}
		});

		HBox buttons = new HBox();
		buttons.getChildren().addAll(fertig, cancel);
		mainBox.add(buttons, 0, 2);
		buttons.setAlignment(Pos.BOTTOM_CENTER);
		buttons.setSpacing(10);

		Scene sc = new Scene(mainBox, 350, 140);
		this.setTitle("Mitglied löschen");
		this.setScene(sc);
		this.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		this.show();

	}

}
