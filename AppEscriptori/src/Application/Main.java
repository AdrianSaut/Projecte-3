package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	private static Stage stg;
	
	@Override
	public void start(Stage primaryStage) {
			try {
				stg = primaryStage;
				Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
				primaryStage.setResizable(false);
				primaryStage.setTitle("DeliverChat");
				primaryStage.setScene(new Scene(root, 600, 400));
				primaryStage.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public void cambiarEscena(String fxml) throws IOException {
		Parent pane = FXMLLoader.load(getClass().getResource(fxml));
		stg.getScene().setRoot(pane);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
