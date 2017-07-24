package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
 * Dialog zum Abbuchen von Guthaben durch Erwerb von Getränken.
 * 
 * @author Juri Dispan
 *
 */
public class AbbuchWindow extends Stage {

	public AbbuchWindow(Member member, DbConnection dbcon, TableView<Member> retTabView) {
		GridPane mainBox = new GridPane();

		mainBox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
		mainBox.setPadding(new Insets(25, 25, 25, 25));
		mainBox.setAlignment(Pos.CENTER);
		mainBox.setHgap(10);
		mainBox.setVgap(10);
		mainBox.setMaxWidth(100);
		Text title = new Text("Abbuchen");
		title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		mainBox.add(title, 0, 0, 2, 1);

		Label alk = new Label("Anzahl Alkoholisches:");
		mainBox.add(alk, 0, 1);
		TextField alkanz = new TextField("0");
		mainBox.add(alkanz, 1, 1);
		alkanz.setMaxWidth(100);
		Label antalk = new Label("Anzahl Antialk:");
		mainBox.add(antalk, 0, 2);
		TextField antalkanz = new TextField("0");
		antalkanz.setMaxWidth(100);
		mainBox.add(antalkanz, 1, 2);

		Button fertig = new Button("Fertig");
		fertig.setPrefSize(110, 20);
		fertig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					int _alk = Integer.parseInt(alkanz.getText());
					int _aalk = Integer.parseInt(antalkanz.getText());
					dbcon.updateStats(member.getName(), _alk, _aalk);
					member.setAlk(member.getAlk() + _alk);
					member.setAntalk(member.getAntalk() + _aalk);
					member.setGuthaben(member.getGuthaben() - _aalk - (1.5 * _alk));
					retTabView.refresh();
					close();
				} catch (NumberFormatException e) {
					// TODO Error
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

		HBox buttons = new HBox(10);
		buttons.setAlignment(Pos.BOTTOM_CENTER);
		buttons.getChildren().addAll(fertig, cancel);
		mainBox.add(buttons, 0, 4);
		Scene sc = new Scene(mainBox, 350, 200);
		this.setTitle("Abbuchen");
		this.setScene(sc);
		this.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		this.show();

	}

}
