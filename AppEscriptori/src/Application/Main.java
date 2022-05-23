package Application;

public class Main extends Application {
    
    Stage window;
    String address;
    int port;
@Override
    public void start(Stage primaryStage) throws Exception{
address = super.getParameters().getRaw().get(0);
        port = Integer.parseInt(super.getParameters().getRaw().get(1));
        //Window
        this.window = primaryStage;
        this.window.setTitle("Client");
        //Grid
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        //set up columns
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(80);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(20);

        //set up rows
        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(75);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(20);
        RowConstraints row2 = new RowConstraints();
        row1.setPercentHeight(20);

        //add columns and rows to the grid
        grid.getRowConstraints().addAll(row0, row1, row2);
        grid.getColumnConstraints().addAll(column0, column1);
        //Scene
        Scene scene = new Scene(grid, 600, 520);
        scene.getStylesheets().add("css/main.css");
        //Client Stuff
        new Client(address, port, grid);
        //Finish Window
        this.window.setScene(scene);
        this.window.show();
}

    public static void main(String[] args) {

        launch(args);
    }
}