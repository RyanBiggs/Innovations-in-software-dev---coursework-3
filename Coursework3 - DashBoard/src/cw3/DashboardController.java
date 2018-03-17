package cw3;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DashboardController implements Initializable
{
    private static String markup;
    private static List<Vehicles> vehicles;
    private static List<String> strings;
    private DashService service;
    
    // Delcare Checkboxes
    private CheckBox[] checkBoxes;

    // Delclare radio buttons and toggle group
    @FXML
    private RadioButton radioBubble, radioPie, radioLine;
    @FXML
    private ToggleGroup myGroup;
    @FXML
    private HBox HBox1, Hbox2;
    @FXML
    private AnchorPane AnchorPane1;

    // Declare barchart and axes for barchart
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private BarChart<?, ?> BarChart1;

    // Declare linechart and axes
    @FXML
    private LineChart<?, ?> lineChart1;
    @FXML
    private NumberAxis lineNoAxis;

    // Declare bubble chart and axes
    @FXML
    private BubbleChart<?, ?> bubbleChart1;
    @FXML
    private NumberAxis bubbleNoAxisL;
    @FXML
    private NumberAxis bubbleNoAxisB;
    
    // Declare piechart
    @FXML
    private PieChart pieChart1;
    
    // Declare menubar
    @FXML
    private Button refresh = new Button();
    @FXML
    private Label dateTime;
    
    // Declare progress indicator
    @FXML
    private ProgressIndicator progressIndicator;
    
    // Declare table
    @FXML
    private TableView<Vehicles> table;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        //retrieves JSON from URL and intialises it into a string, GSON takes JSON string and applies it to a list
        service = new DashService();
        service.setAddress("http://glynserver.cms.livjm.ac.uk/DashService/SalesGetSales");
                
        // Sets progress indicator to indeterminate. I.e. Removes percentage display.
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        
        // Progress indicator is visible only when the service is runing
        progressIndicator.visibleProperty().bind(service.runningProperty());
        
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() 
        {
            @Override
            public void handle(WorkerStateEvent e) 
            {
                markup = e.getSource().getValue().toString();

                vehicles = (new Gson()).
                        fromJson(markup,
                                new TypeToken<ObservableList<Vehicles>>() 
                                {
                                    
                                }.getType());
                
                System.out.print(vehicles);

                strings = vehicles.stream()
                        .map(object -> object.getYearString())
                        .distinct()
                        .collect(Collectors.toList());
                
                constructCheckBoxes();
                constructRadio();
                constructMenuBar();
                constructTable();
                
                refresh.setDisable(false);
            }
        });

        service.start();
        
        // OnClick for refresh button
        refresh.setOnAction((ActionEvent e) -> 
        {
            System.out.println("Refreshing Data!");
            refresh.setDisable(true);
            service.restart();
            
        });
    }

    //constructs checkboxes for the barchart
    private void constructCheckBoxes()
    {        
        if (Arrays.toString(checkBoxes).isEmpty())
        {
            
        }
        
        
        System.out.print(checkBoxes);
        checkBoxes = new CheckBox[strings.size()];
        
        //loops through data set creating a new check box for each year in the table
        for (byte index = 0; index < strings.size(); index++)
        {
            checkBoxes[index] = new CheckBox(strings.get(index));
            checkBoxes[index].setSelected(false);
            checkBoxes[index].addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent e) 
                {
                    System.out.println("Firstly, Event Filters !");
                }
            });
            
            checkBoxes[index].addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent e)
                {
                    System.out.println("Secondly, Event Handlers !");
                }
            });
            
            checkBoxes[index].setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent e)
                {
                    System.out.println("Thirdly, Convenience Methods !");

                    constructSeries();
                }
            });
            
            //adds the constructed check boxes to a hbox
            HBox1.getChildren().add(checkBoxes[index]);
        }
        AnchorPane1.getScene().getWindow().sizeToScene();
    }

    //constructs the data set for the barchart
    private void constructSeries()
    {
        BarChart1.getData().clear();

        for (CheckBox checkBox : checkBoxes)
        {
            if (checkBox.isSelected())
            {
                XYChart.Series series = new XYChart.Series();
                series.setName(checkBox.getText());

                for (Vehicles vehicle : vehicles)
                {
                    if (vehicle.getYearString().equals(checkBox.getText()))
                    {
                        series.getData().add(new XYChart.Data<>(vehicle.getVehicle(), vehicle.getQuantity()));
                    }
                }
                
                BarChart1.getData().add(series);
            }
        }
    }

    //Provides functionality to radio buttons and allows the changing of the graph being viewed
    private void constructRadio()
    {
        myGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle)
            {
                if (myGroup.getSelectedToggle().equals(radioLine))
                {
                    lineChart1.setVisible(true);
                    bubbleChart1.setVisible(false);
                    pieChart1.setVisible(false);
                } 
                else if (myGroup.getSelectedToggle().equals(radioBubble))
                {
                    lineChart1.setVisible(false);
                    bubbleChart1.setVisible(true);
                    pieChart1.setVisible(false);
                }
                else if (myGroup.getSelectedToggle().equals(radioPie))
                {
                    lineChart1.setVisible(false);
                    bubbleChart1.setVisible(false);
                    pieChart1.setVisible(true);
                }
            }
        });
    }

    //not quite sure how some of this work since its from glyns stuff
    //todo change comment above
    private static class DashService extends Service<String>
    {
        private StringProperty address = new SimpleStringProperty();

        public final void setAddress(String address)
        {
            this.address.set(address);
        }

        public final String getAddress()
        {
            return address.get();
        }

        public final StringProperty addressProperty()
        {
            return address;
        }

        @Override
        protected Task<String> createTask()
        {
            return new Task<String>()
            {
                private URL url;
                private HttpURLConnection connect;
                private String markup = "";

                @Override
                protected String call()
                {
                    try
                    {
                        url = new URL(getAddress());
                        connect = (HttpURLConnection) url.openConnection();
                        connect.setRequestMethod("GET");
                        connect.setRequestProperty("Accept", "application/json");
                        connect.setRequestProperty("Content-Type", "application/json");

                        markup = (new BufferedReader(new InputStreamReader(connect.getInputStream()))).readLine();
                    } 
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (connect != null)
                            connect.disconnect();
                    }
                    
                    return markup;
                }
            };
        }
    }
    
    private void constructMenuBar()
    {        
        // Code for datetime
        dateTime();
        
        // Print button?
        // Export data button?
    }
    
    private void dateTime()
    {
        Task Task1 = new Task<Void>() 
        {
            @Override
            public Void call() throws InterruptedException 
            {
                while (true) 
                {
                    if (isCancelled()) 
                    {
                        break;
                    }

                    this.updateMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("d MMM yyyy hh:mm:ss"))); // NOTE : 5 Oct 2015 20:20:20 !
                }

                return null;
            }
        };
        
        Task1.messageProperty().addListener(new ChangeListener() 
        {
            @Override
            public void changed(ObservableValue o, Object ov, Object nv) 
            {
                dateTime.setText((String) o.getValue());
            }
        });

        new Thread(Task1).start();
    }
    
    private void constructTable()
    {
        //https://shahsparx.me/javafx-tableview-example-tableview-json/ try this
        
        // Region column
        TableColumn<Vehicles, String> regionCol = new TableColumn<>("Region"); 
        regionCol.setMinWidth(200);
        regionCol.setCellValueFactory(new PropertyValueFactory<>("Region"));
        
        // Vehicle column
        TableColumn<Vehicles, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setMinWidth(200);
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("Vehicle"));
        
        // Quantity column
        TableColumn<Vehicles, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setMinWidth(200);
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        
        // Year column
        TableColumn<Vehicles, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setMinWidth(200);
        yearCol.setCellValueFactory(new PropertyValueFactory<>("Year"));
        
        // Quarter column
        TableColumn<Vehicles, Byte> quarterCol = new TableColumn<>("Quarter");
        quarterCol.setMinWidth(200);
        quarterCol.setCellValueFactory(new PropertyValueFactory<>("Qtr"));
        
        table = new TableView<>();
        table.setItems(getVehicles());
        table.getColumns().addAll(regionCol, vehicleCol, quantityCol, yearCol, quarterCol);
    }
    
    public ObservableList<Vehicles> getVehicles()
    {
        byte b = 2;
        ObservableList<Vehicles> vehiclesList = FXCollections.observableArrayList();
        vehiclesList.add(new Vehicles("America", "Elise", 24, 2014, b));
        vehiclesList.add(new Vehicles("America2", "Elise5", 27, 2013, b));
        vehiclesList.add(new Vehicles("America3", "Elise2", 25, 2012, b));
        vehiclesList.add(new Vehicles("America4", "Elise4", 15, 2013, b));
        vehiclesList.add(new Vehicles("America5", "Elise6", 17, 2014, b));
        
        return vehiclesList;
    }
}
