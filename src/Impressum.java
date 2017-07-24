package application;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Nur zum Spaﬂ.
 * 
 * @author Juri Dispan
 *
 */
public class Impressum extends Stage {
	static int i = 0;

	public Impressum() {

		Box box = new Box(4, 4, 4);
		box.setMaterial(new PhongMaterial(Color.CHARTREUSE));
		// box.setDrawMode(DrawMode.LINE);
		PerspectiveCamera cam = new PerspectiveCamera(true);

		cam.getTransforms().addAll(new Rotate(0, Rotate.Y_AXIS), new Rotate(0, Rotate.X_AXIS),
				new Translate(0, 0, -15));
		Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
		Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
		Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
		rxBox.setAngle(45);
		ryBox.setAngle(45);
		rzBox.setAngle(0);
		box.getTransforms().addAll(rxBox, ryBox, rzBox);

		RotateTransition rt = new RotateTransition(Duration.millis(5000), box);
		rt.setByAngle(360);
		rt.setCycleCount(RotateTransition.INDEFINITE);
		rt.setInterpolator(Interpolator.LINEAR);
		rt.setAxis(new Point3D(0, 1, 0));
		rt.play();

		Group root = new Group();
		root.getChildren().addAll(cam, box);

		SubScene sub = new SubScene(root, 350, 350);
		sub.setFill(Color.ALICEBLUE);
		sub.setCamera(cam);
		Group g = new Group();
		g.getChildren().add(sub);

		BorderPane bp = new BorderPane();

		bp.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));

		Label label = new Label("by Juri Dispan 2017");
		HBox hb = new HBox();
		hb.getChildren().add(label);
		hb.setSpacing(10);
		hb.setAlignment(Pos.TOP_CENTER);
		label.setScaleX(2);
		label.setScaleY(2);
		label.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (i++ % 2 == 0) {
					label.setText("Bei Bugs: juri.dispan@me.com");
				} else {
					label.setText("by Juri Dispan 2017");
				}

			}
		});

		Button ok = new Button("Ok");
		ok.setAlignment(Pos.BOTTOM_CENTER);
		ok.setPrefSize(500, 20);
		ok.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				close();
			}
		});
		ok.setDefaultButton(true);
		bp.setTop(hb);
		bp.setBottom(ok);
		bp.setCenter(g);

		Scene sc = new Scene(bp, 500, 500);

		this.setTitle("Impressum");
		this.setScene(sc);
		this.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		this.show();

	}

}
