import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;


public class GUI extends Application {
    private double mouse_pos_x, mouse_pos_y;
    private double mouse_old_x, mouse_old_y;
    private double old_y, old_X;
    private static Optimizer optimize;
    private static Sphere[] points;
    private static double scale;
    private final Rotate rotate_x = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotate_y = new Rotate(0, Rotate.Y_AXIS);
    private double time = 0;
    private double dt = 0;
    private int p_id = 0;
    private int focus = -1;
    //start res
    int def_x_res = 1024;
    int def_y_res = 768;
    int scene_ratio = 5;

    Text pos = new Text("-");
    Text speed = new Text("-");

    double cspeed = 1; // how many calcs pr print

    private boolean trail_line = false;
    private int trail_line_s = 9;
    private int trail_line_n = trail_line_s;

    private double[][] prev_points;

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        //animation
        Group sat_animation_graph = new Group();
        //line animation
        Group line_animation = new Group();
        //rotaion of animation
        Group sat_animation_rotation = new Group(sat_animation_graph,line_animation);
        //animation camra group
        Group sat_animation_cam = new Group(sat_animation_rotation);


        sat_animation_rotation.maxHeight(def_y_res);
        sat_animation_rotation.maxWidth((scene_ratio-1) * def_x_res / scene_ratio);

        SubScene sat_animation_scene = new SubScene(sat_animation_cam, (scene_ratio-1) * def_x_res / scene_ratio,def_y_res,true,SceneAntialiasing.BALANCED);
        //animation gui
        Group sat_animation_gui = new Group(sat_animation_scene);
        // Background colour of sim
        sat_animation_scene.setFill(Color.web("#000000"));

        //sim camera
        PerspectiveCamera g_cam = new PerspectiveCamera(true);
        g_cam.setNearClip(0.01);
        g_cam.setFarClip(1000000.0);
        g_cam.setTranslateZ(-1000);
        sat_animation_cam.getChildren().add(g_cam);
        sat_animation_scene.setCamera(g_cam);

        VBox left_content = new VBox();
        Group left_root = new Group(left_content);

        SubScene left_scene = new SubScene(left_root, def_x_res / scene_ratio,def_y_res,true,SceneAntialiasing.BALANCED);

        HBox scenes = new HBox(left_scene,sat_animation_gui);

        //main scene
        Scene main_scene = new Scene(scenes, def_x_res, def_y_res, true, SceneAntialiasing.BALANCED);

        // -----------------------------------
        // ------------ content --------------
        // -----------------------------------

        // Sim Timer
        Text timer_s_t = new Text("Seconds since start");
        timer_s_t.setFont(Font.font("Arial",26));
        Text timer_s = new Text("-");
        timer_s.setFont(Font.font("Arial",26));
        Text timer_h_t = new Text("Days since start");
        timer_h_t.setFont(Font.font("Arial",26));
        Text timer_h = new Text("-");
        timer_h.setFont(Font.font("Arial",26));
        left_content.getChildren().add(timer_s_t);
        left_content.getChildren().add(timer_s);
        left_content.getChildren().add(timer_h_t);
        left_content.getChildren().add(timer_h);

        Text timer_dt_t = new Text("Dt");
        timer_dt_t.setFont(Font.font("Arial",26));
        Text timer_dt = new Text("-");
        timer_dt.setFont(Font.font("Arial",26));
        left_content.getChildren().add(timer_dt_t);
        left_content.getChildren().add(timer_dt);

        // focus ref pos and speed
        Text over = new Text("Position and speed of current focus");
        over.setFont(Font.font("Arial",26));
        left_content.getChildren().add(over);

        pos.setFont(Font.font("Arial",26));

        left_content.getChildren().add(pos);

        //print per x_calc

        Button slow = new Button("/2");
        Button fast = new Button("x2");
        Text over_s= new Text("Speed");
        Text cur = new Text(String.valueOf(cspeed));
        over_s.setFont(Font.font("Arial",26));
        cur.setFont(Font.font("Arial",26));

        HBox x_cal = new HBox(slow,fast,cur);

        slow.setOnAction(event -> {
            if(cspeed > 1) {
                cspeed /= 2;
                cur.setText(String.valueOf(cspeed));
            }
        });
        fast.setOnAction(event -> {
            cspeed *= 2;
            cur.setText(String.valueOf(cspeed));
        });

        left_content.getChildren().addAll(over_s,x_cal);



        // Add 3D axes
        add_axes(sat_animation_graph);

        // Generate graph points
        generate_scatter_start_points(sat_animation_graph,sat_animation_gui);


        // Trail toggle
        ToggleButton trail_toggle = new ToggleButton("Trail Line");

        trail_toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                trail_line = true;
            } else {
                trail_line = false;
                line_animation.getChildren().clear();
            }
        });

        left_content.getChildren().add(trail_toggle);



        // -----------------------------------------------
        // ------------ screen manipulation --------------
        // -----------------------------------------------

        // Mouse event handling for rotating the plot
        sat_animation_rotation.getTransforms().addAll(rotate_x, rotate_y);

        sat_animation_scene.setOnMousePressed(event -> {
            mouse_old_x = event.getSceneX();
            mouse_old_y = event.getSceneY();
            old_y = rotate_y.getAngle();
            old_X = rotate_x.getAngle();
        });

        sat_animation_scene.setOnMouseDragged(event -> {
            mouse_pos_x = event.getSceneX();
            mouse_pos_y = event.getSceneY();
            rotate_y.setAngle(old_y + (mouse_old_x - mouse_pos_x) );
            rotate_x.setAngle(old_X - (mouse_old_y - mouse_pos_y) );
        });
        // Track width changes
        main_scene.widthProperty().addListener((observable, old_val, new_val) -> {
            sat_animation_scene.setWidth((scene_ratio-1) * new_val.doubleValue() / scene_ratio);
            left_scene.setWidth(new_val.doubleValue() / scene_ratio);
        });

        // Track height changes
        main_scene.heightProperty().addListener((observable, old_val, new_val) -> {
            sat_animation_scene.setHeight(new_val.doubleValue());
            left_scene.setHeight(new_val.doubleValue());
        });
        //zoom
        sat_animation_scene.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            double mod = 1.1;
            if(delta < 0){
                scale /= mod;
            }
            if(delta > 0){
                scale *= mod;

            }
            line_animation.getChildren().clear();
        });

        AnimationTimer animate = new AnimationTimer() {
            @Override
            public void handle(long l) {
                uppdate_scatter_data(line_animation);
                timer_s.setText(String.valueOf(time));
                timer_h.setText(String.valueOf(time/(60*60*24)));
                timer_dt.setText(String.valueOf(dt));
            }
        };
        animate.start();

        primaryStage.setTitle("");
        primaryStage.setScene(main_scene);
        primaryStage.show();

    }

    private void add_axes(Group group) {
        // Origin lines for X, Y, Z
        Box ax_x = new Box(500, 2, 2);
        ax_x.setMaterial(new PhongMaterial(Color.RED));

        Box ax_y = new Box(2, 500, 2);
        ax_y.setMaterial(new PhongMaterial(Color.GREEN));

        Box ax_z = new Box(2, 2, 500);
        ax_z.setMaterial(new PhongMaterial(Color.BLUE));

        group.getChildren().addAll(ax_x, ax_y, ax_z);
    }

    private void generate_scatter_start_points(Group ani_graph, Group ani_gui) {
        double[][][] print_values = optimize.get_print_values(0);
        prev_points = new double[print_values[p_id].length][3];
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
            prev_points[i] = new double[]{x,y,z};
            ani_graph.getChildren().add(point);

            // focus buttons
            Button focus_button = new Button("id: " + i);
            int finalI = i;
            focus_button.setOnAction(event -> {
                focus = finalI;
            });
            focus_buttons.getChildren().add(focus_button);

        }
        focus_buttons.setTranslateX(5);
        focus_buttons.setTranslateY(10);
        ani_gui.getChildren().add(focus_buttons);
    }

    private void uppdate_scatter_data(Group line_animation) {
        time = (int) optimize.get_print_times()[0];
        dt = optimize.get_dt();
        double[][][] print_values = optimize.get_print_values((int) cspeed).clone();

        for (int i = 0; i < points.length; i++) {
            double tempx = print_values[p_id][i][0];
            double tempy = print_values[p_id][i][1];
            double tempz = print_values[p_id][i][2];

            if (focus > -1 && focus < print_values[p_id].length){
                tempx -= print_values[p_id][focus][0];
                tempy -= print_values[p_id][focus][1];
                tempz -= print_values[p_id][focus][2];
            }
            if(i == focus){
                double temp_pos = Math.sqrt(Math.pow(print_values[p_id][i][0],2)+Math.pow(print_values[p_id][i][1],2)+Math.pow(print_values[p_id][i][2],2));
                pos.setText(String.valueOf(temp_pos));
            }

            if(trail_line && trail_line_n == 0) {
                Sphere line = new Sphere(2);

                PhongMaterial material = new PhongMaterial();
                material.setDiffuseColor(Color.color(Math.random(), Math.random(), Math.random()));
                line.setMaterial(material);

                line.setTranslateX(prev_points[i][0] * scale);
                line.setTranslateZ(prev_points[i][1] * scale);
                line.setTranslateY(prev_points[i][2] * scale);

                prev_points[i] = new double[]{tempx, tempy, tempz};

                line_animation.getChildren().add(line);
            }

            points[i].setTranslateX(tempx * scale);
            points[i].setTranslateZ(tempy * scale);
            points[i].setTranslateY(tempz * scale);
        }
        if (trail_line_n == 0){
            trail_line_n = trail_line_s;
        }
        else {
            trail_line_n--;
        }
    }

    public void starter(Optimizer optimizer){
        optimize = optimizer;
        launch();
    }
}
