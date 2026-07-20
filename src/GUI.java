import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;


public class GUI extends Application {
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private static Optimizer optimize;
    private static Sphere[] points;
    private static double scale;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private double time = 0;
    private int p_id = 0;
    private int focus = -1;
    //start res
    int def_x_res = 1024;
    int def_y_res = 768;

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        //animation
        Group sat_animation_graph = new Group();
        //rotaion of animation
        Group sat_animation_rotation = new Group(sat_animation_graph);
        //animation gui
        Group sat_animation_gui = new Group(sat_animation_rotation);
        //Static GUI
        Group root = new Group(sat_animation_gui);
        // Add 3D axes
        addAxes(sat_animation_graph);

        // Generate scatter points
        generateScatterData(sat_animation_graph,sat_animation_gui);

        // Setup the camera and coordinate transformations

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.translateZProperty().set(-1000);

        // Wrap the root for scene-specific rotation/translation

        sat_animation_rotation.getChildren().add(camera);




        Text timer = new Text("");
        timer.setX(10);
        timer.setY(10);
        root.getChildren().add(timer);


        Scene scene = new Scene(root, def_x_res, def_y_res, true, SceneAntialiasing.BALANCED);
        scene.setCamera(new PerspectiveCamera());

        //scene.setCamera(camera);
        sat_animation_rotation.setTranslateX(def_x_res / 2);
        sat_animation_rotation.setTranslateY(def_y_res / 2);

        // Mouse event handling for rotating the plot
        sat_animation_rotation.getTransforms().addAll(rotateX, rotateY);
        scene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            double modifier = 5.0;
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            rotateY.setAngle(sat_animation_rotation.getRotate() + (mouseOldX - mousePosX) / modifier);
            rotateX.setAngle(sat_animation_rotation.getRotate() - (mouseOldY - mousePosY) / modifier);
        });
        // Track width changes
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            sat_animation_rotation.setTranslateX(newValue.doubleValue() / 2);
            camera.setTranslateX(newValue.doubleValue() / 2);
        });

        // Track height changes
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            sat_animation_rotation.setTranslateY(newValue.doubleValue() / 2);
            camera.setTranslateY(newValue.doubleValue() / 2);
        });
        //zoom
        scene.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            if(delta < 0){
                scale /= (Math.abs(delta)/10);
            }
            if(delta > 0){
                scale *= Math.abs(delta) / 10;
            }
        });

        AnimationTimer animate = new AnimationTimer() {
            @Override
            public void handle(long l) {
                uppdate_scatter_data();
                timer.setText(String.valueOf(time));
            }
        };
        animate.start();

        primaryStage.setTitle("JavaFX 3D Scatter Plot");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void addAxes(Group group) {
        // Origin lines for X, Y, Z
        Box xAxis = new Box(500, 2, 2);
        xAxis.setMaterial(new PhongMaterial(Color.RED));

        Box yAxis = new Box(2, 500, 2);
        yAxis.setMaterial(new PhongMaterial(Color.GREEN));

        Box zAxis = new Box(2, 2, 500);
        zAxis.setMaterial(new PhongMaterial(Color.BLUE));

        group.getChildren().addAll(xAxis, yAxis, zAxis);
    }

    private void generateScatterData(Group ani_graph, Group ani_gui) {
        double[][][] print_values = optimize.get_print_values();
        points = new Sphere[print_values[p_id].length];
        VBox focus_buttons = new VBox(10);
        double dist_s = 0;
        if(print_values[p_id].length > 1) {
            dist_s = Math.sqrt(Math.pow(print_values[p_id][1][0], 2) + Math.pow(print_values[p_id][1][1], 2) + Math.pow(print_values[p_id][1][2], 2));
        }
        scale = def_y_res / dist_s;
        for (int i = 0; i < print_values[p_id].length; i++) {
            // graph points
            double x = print_values[p_id][i][0] * scale;
            double y = print_values[p_id][i][1] * scale;
            double z = print_values[p_id][i][2] * scale;

            Sphere point = new Sphere(5);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(Color.color(Math.random(), Math.random(), Math.random()));
            point.setMaterial(material);

            point.setTranslateX(x);
            point.setTranslateY(y);
            point.setTranslateZ(z);
            points[i] = point;
            ani_graph.getChildren().add(point);

            // focus buttons
            Button focus_button = new Button("id: " + i);
            int finalI = i;
            double dist_t = Math.sqrt(Math.pow(print_values[p_id][i][0], 2) + Math.pow(print_values[p_id][i][1], 2) + Math.pow(print_values[p_id][i][2], 2));
            if(print_values[p_id].length > i+1) {
                dist_t = -dist_t + Math.sqrt(Math.pow(print_values[p_id][i+1][0], 2) + Math.pow(print_values[p_id][i+1][1], 2) + Math.pow(print_values[p_id][i+1][2], 2));
            }
            else{
                dist_t -= Math.sqrt(Math.pow(print_values[p_id][i-1][0], 2) + Math.pow(print_values[p_id][i-1][1], 2) + Math.pow(print_values[p_id][i-1][2], 2));
            }

            double finalD = dist_t;
            focus_button.setOnAction(event -> {
                focus = finalI;
                scale = def_y_res / finalD;
            });
            focus_buttons.getChildren().add(focus_button);

        }
        ani_gui.getChildren().add(focus_buttons);
    }

    private void uppdate_scatter_data() {
        double[][][] print_values = optimize.get_print_values().clone();
        time = (int) optimize.get_print_times()[0];
        for (int i = 0; i < points.length; i++) {
            double tempx = print_values[p_id][i][0];
            double tempy = print_values[p_id][i][1];
            double tempz = print_values[p_id][i][2];
            if (focus > -1 && focus < print_values[p_id].length){
                tempx -= print_values[p_id][focus][0];
                tempy -= print_values[p_id][focus][1];
                tempz -= print_values[p_id][focus][2];
            }
            points[i].setTranslateX(tempx * scale);
            points[i].setTranslateZ(tempy * scale);
            points[i].setTranslateY(tempz * scale);
        }
    }

    public void starter(Optimizer optimizer){
        optimize = optimizer;
        launch();
    }
}
