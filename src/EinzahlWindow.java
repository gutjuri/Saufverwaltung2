import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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
 * Dialog zum Einmzahlen von Guthaben.
 * 
 * @author Juri Dispan
 *
 */
public class EinzahlWindow extends Stage {

	public EinzahlWindow(Member member, DbConnection dbcon, TableView<Member> retTabView) {
		GridPane mainBox = new GridPane();

		mainBox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
		mainBox.setPadding(new Insets(25, 25, 25, 25));
		mainBox.setAlignment(Pos.CENTER);
		mainBox.setHgap(10);
		mainBox.setVgap(10);
		mainBox.setMaxWidth(100);
		Text title = new Text(Main.msg.getString("deposit"));
		title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		mainBox.add(title, 0, 0, 2, 1);

		Label betrag = new Label(Main.msg.getString("amount") + ": (€) ");
		mainBox.add(betrag, 0, 1);
		TextField betrFeld = new TextField("0");
		mainBox.add(betrFeld, 1, 1);
		betrFeld.setPrefWidth(100);

		Button fertig = new Button(Main.msg.getString("ok"));
		fertig.setPrefSize(110, 20);
		fertig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String str = betrFeld.getText().replaceAll(",", ".");
				try {
					double betr = Double.parseDouble(str);
					dbcon.deposit(member.getName(), betr);
					member.setGuthaben(member.getGuthaben() + betr);
					retTabView.refresh();
					close();
				} catch (NumberFormatException e) {
					makeAlert(str);
				}

			}
		});
		fertig.setDefaultButton(true);

		Button cancel = new Button(Main.msg.getString("cancel"));
		cancel.setPrefSize(110, 20);
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

		Scene sc = new Scene(mainBox, 350, 150);
		this.setTitle("Einzahlen");
		this.setScene(sc);
		this.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		this.show();

	}

	public void makeAlert(String inv) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Main.msg.getString("error"));
		alert.setHeaderText("\"" + inv + "\" " + Main.msg.getString("isnonum") + ".");
		alert.setContentText(Main.msg.getString("plsnum") + ".");
		alert.showAndWait();
	}

}
