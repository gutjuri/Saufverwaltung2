package saufverwaltung.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import saufverwaltung.control.Controller;
import saufverwaltung.util.Localizer;
import saufverwaltung.util.RefreshingTable;

/**
 * Dialog zum L�schen eines Mitgliedes aus der Datenbank.
 * 
 * @author Juri Dispan
 *
 */
public class DeleteWindow extends Stage {

    public DeleteWindow(RefreshingTable tab, Controller ctl, Localizer localizer) {
        GridPane mainBox = new GridPane();
        Scene sc = new Scene(mainBox, 350, 140);
        sc.getStylesheets().add("application.css");

        mainBox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
        mainBox.setPadding(new Insets(25, 25, 25, 25));
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setHgap(10);
        mainBox.setVgap(10);
        mainBox.setMaxWidth(100);
        Text title = new Text(localizer.getString("delmember"));
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        mainBox.add(title, 0, 0, 2, 1);

        Label dellabel = new Label(localizer.getString("whichmem"));
        mainBox.add(dellabel, 0, 1);
        TextField betrFeld = new TextField("");
        mainBox.add(betrFeld, 1, 1);
        betrFeld.setPrefWidth(100);

        Button fertig = new Button(localizer.getString("delete"));
        fertig.setPrefSize(150, 20);
        fertig.getStyleClass().add("deleteButton");
        fertig.setOnAction(e -> ctl.handleDeleteMember(this, betrFeld, tab));

        Button cancel = new Button(localizer.getString("cancel"));
        cancel.setPrefSize(150, 20);
        cancel.setOnAction(e -> close());

        HBox buttons = new HBox();
        buttons.getChildren().addAll(fertig, cancel);
        mainBox.add(buttons, 0, 2);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setSpacing(10);

        this.setTitle(localizer.getString("delmember"));
        this.setScene(sc);
        this.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        this.show();

    }

}
