import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
 * Dialog zum Umbenennen eines Nutzers.
 * 
 * @author Juri Dispan
 *
 */
public class RenameWindow extends Stage {

	public RenameWindow(DbConnection dbcon, TableView<Member> retTabView) {
		GridPane mainBox = new GridPane();

		mainBox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
		mainBox.setPadding(new Insets(25, 25, 25, 25));
		mainBox.setAlignment(Pos.CENTER);
		mainBox.setHgap(10);
		mainBox.setVgap(10);
		mainBox.setMaxWidth(100);
		Text title = new Text("Nutzer umbenennen");
		title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		mainBox.add(title, 0, 0, 2, 1);

		Label labelChoose = new Label("Nutzer wählen:");
		mainBox.add(labelChoose, 0, 1);
		ComboBox<Member> cbox = new ComboBox<>(retTabView.getItems());
		mainBox.add(cbox, 1, 1);
		cbox.setMaxWidth(100);
		Label antalk = new Label("Neuer Name:");
		mainBox.add(antalk, 0, 2);
		TextField tfield = new TextField();
		tfield.setMaxWidth(100);
		mainBox.add(tfield, 1, 2);

		Button fertig = new Button("Fertig");
		fertig.setPrefSize(110, 20);
		fertig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Member cmember = cbox.getValue();
				String oldname = cmember.getName();
				String newname = tfield.getText();
				dbcon.renameUser(oldname, newname);
				cmember.setName(newname);
				retTabView.refresh();
				close();
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

		HBox buttons = new HBox(10);
		buttons.setAlignment(Pos.BOTTOM_CENTER);
		buttons.getChildren().addAll(fertig, cancel);
		mainBox.add(buttons, 0, 4);
		Scene sc = new Scene(mainBox, 350, 200);
		this.setTitle("Nutzer umbenennen");
		this.setScene(sc);
		this.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		this.show();

	}

}
