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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
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
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DashboardController implements Initializable
{
    private static String markup;
    private static List<Vehicles> vehicles;
    private static List<String> yearStrings, vehicleStrings, regionStrings;
    private DashService service;
    
    // Delcare Checkboxes
    private CheckBox[] yearCheckBoxes;
    
    // Delclare radio buttons and toggle group
    @FXML
    private RadioButton radioBar1, radioPie1, radioBar2, radioPie2, radioBar3, radioPie3;
    @FXML
    private ToggleGroup myGroup1, myGroup2, myGroup3;
    @FXML
    private HBox yearCheckBoxesContainer, quarterlySalesChartType, regionalSalesChartType;
    @FXML
    private AnchorPane AnchorPane1;

    // Declare barchart and axes for barchart
    @FXML
    private BarChart<?, ?> barChart1;
    @FXML
    private NumberAxis barChart1YAxis;
    @FXML
    private CategoryAxis barChart1XAxis;
    
    @FXML
    private StackedBarChart<?, ?> barChart2;
    @FXML
    private NumberAxis barChart2YAxis;
    @FXML
    private CategoryAxis barChart2XAxis;
    
    @FXML
    private StackedBarChart<?, ?> barChart3;
    @FXML
    private NumberAxis barChart3YAxis;
    @FXML
    private CategoryAxis barChart3XAxis;


    // Declare linechart and axes
    @FXML
    private LineChart<?, ?> lineChart2;
    @FXML
    private CategoryAxis lineXAxis;
    @FXML
    private NumberAxis lineYAxis;
    
    // Declare piechart
    @FXML
    private PieChart pieChart1;
    @FXML
    private PieChart pieChart2;
    @FXML
    private PieChart pieChart3;
    
    // Declare menubar
    @FXML
    private Button refresh = new Button();
    @FXML
    private Label dateTime;
    
    // Declare progress indicator
    @FXML
    private ProgressIndicator progressIndicator;  

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

                Gson gson = new Gson();
                
                vehicles = gson.fromJson( markup, new TypeToken<ObservableList<Vehicles>>(){}.getType() );
                
                // DEBUG
                vehicles.add(new Vehicles("America", "Elise", 100, 2015, (byte)1)); // NOTE : Insert Data
                vehicles.add(new Vehicles("America", "Elise", 100, 2015, (byte)2)); // NOTE : Insert Data
                vehicles.add(new Vehicles("America", "Elise", 200, 2015, (byte)3)); // NOTE : Insert Data
                vehicles.add(new Vehicles("America", "Elise", 1000, 2015, (byte)4)); // NOTE : Insert Data

                yearStrings = vehicles.stream()
                        .map(object -> object.getYearString())
                        .distinct()
                        .collect(Collectors.toList());
                
                vehicleStrings = vehicles.stream()
                        .map(object -> object.getVehicle())
                        .distinct()
                        .collect(Collectors.toList());
                
                regionStrings = vehicles.stream()
                        .map(object -> object.getRegion())
                        .distinct()
                        .collect(Collectors.toList());
                
                // DEBUG
                //System.out.println(vehicleStrings);
                //System.out.println("VEHICLES : " + vehicles);
                
                // Clear the year selector checkboxes, so when refreshed duplicates aren't added
                yearCheckBoxesContainer.getChildren().clear();
                
                constructCheckBoxes();
                constructRadio();
                constructMenuBar();
                
                // Re-enable refresh button once refresh is complete
                refresh.setDisable(false);
            }
        });

        service.start();
        
        // OnClick for refresh button
        refresh.setOnAction((ActionEvent e) -> 
        {
            System.out.println("Refreshing Data!");
            
            // Disable refresh button once clicked
            refresh.setDisable(true);
            
            // Disable other stuff
            
            // Restart service
            service.restart();
            
        });
    }

    //constructs checkboxes for the barchart
    private void constructCheckBoxes()
    {        
        yearCheckBoxes = new CheckBox[yearStrings.size()];
        
        //loops through data set creating a new check box for each year in the table
        for (byte index = 0; index < yearStrings.size(); index++)
        {
            yearCheckBoxes[index] = new CheckBox(yearStrings.get(index));
            yearCheckBoxes[index].setSelected(false);
            yearCheckBoxes[index].addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent e) 
                {
                    System.out.println("Firstly, Event Filters !");
                }
            });
            
            yearCheckBoxes[index].addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent e)
                {
                    System.out.println("Secondly, Event Handlers !");
                }
            });
            
            yearCheckBoxes[index].setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent e)
                {
                    System.out.println("Thirdly, Convenience Methods !");

                    constructSeries();
                }
            });
            
            //adds the constructed check boxes to a hbox
            yearCheckBoxesContainer.getChildren().add(yearCheckBoxes[index]);
        }
        AnchorPane1.getScene().getWindow().sizeToScene();
    }

    //****************************
    //  constructSeries - Desc
    //****************************
    private void constructSeries()
    {
        barChart1.getData().clear();
        pieChart1.getData().clear();
        
        barChart2.getData().clear();
        pieChart2.getData().clear();
        
        barChart3.getData().clear();
        pieChart3.getData().clear();

        for (CheckBox checkBox : yearCheckBoxes)
        {
            if (checkBox.isSelected())
            {
                // Total sales bar chart
                XYChart.Series series = new XYChart.Series();
                series.setName(checkBox.getText());
                
                // Total sales pie chart
                

                // Quarterly sales bar chart            
                XYChart.Series seriesQtr1 = new XYChart.Series();
                seriesQtr1.setName(checkBox.getText() + ": Q1");
                
                XYChart.Series seriesQtr2 = new XYChart.Series();
                seriesQtr2.setName(checkBox.getText() + ": Q2");
                
                XYChart.Series seriesQtr3 = new XYChart.Series();
                seriesQtr3.setName(checkBox.getText() + ": Q3");
                
                XYChart.Series seriesQtr4 = new XYChart.Series();
                seriesQtr4.setName(checkBox.getText() + ": Q4");
                
                // quarterly sales pie chart
                PieChart.Data slice1 = null;
                PieChart.Data slice2 = null;
                PieChart.Data slice3 = null;
                PieChart.Data slice4 = null;
                
                // Bar Chart Chart3
                XYChart.Series seriesRegion = new XYChart.Series();
                seriesRegion.setName(checkBox.getText());
                  
                // Bar Chart 1
                for (int i = 0; i < vehicleStrings.size(); i++)
                {                   
                    int count = 0;
                    
                    for (Vehicles vehicle : vehicles)
                    {
                        if( vehicleStrings.get(i).equals(vehicle.getVehicle()) )
                        {
                            if (vehicle.getYearString().equals(checkBox.getText()))
                            {                        
                                count+=vehicle.getQuantity();
                            }
                        }
                    }
                    series.getData().add(new XYChart.Data<>(vehicleStrings.get(i), count));      // Bar chart 1
                                        
                    pieChart1.getData().add(new PieChart.Data(vehicleStrings.get(i) + ": " + checkBox.getText(), count)); 
                } 
                
                barChart1.getData().add(series); 
                
                pieChart1.getData().stream().forEach(data -> 
                {
                    Tooltip tooltip = new Tooltip();
                    tooltip.setText(data.getPieValue() + " Sales");
                    Tooltip.install(data.getNode(), tooltip);
                    data.pieValueProperty().addListener((observable, oldValue, newValue) -> 
                    tooltip.setText(newValue + " Sales"));
                });
                
                // Bar Chart 2
                for (int i = 0; i < yearStrings.size(); i++)
                {                   
                    int[] count = {0,0,0,0};                   
                    
                    for (Vehicles vehicle : vehicles)
                    {
                        if (vehicle.getYearString().equals(checkBox.getText()))
                        {                                               
                            if (vehicle.getYearString().equals(yearStrings.get(i)))
                            {
                                count[vehicle.getQTR() - 1] += vehicle.getQuantity();
                            }
                        }
                    }
                    if (count[0] == 0 && count[1] == 0 && count[2] == 0 && count[3] == 0)
                        continue;
                    
                    // Chart set 2
                    seriesQtr1.getData().add(new XYChart.Data<>(yearStrings.get(i), count[0]));     // Bar chart 2 Q1
                    seriesQtr2.getData().add(new XYChart.Data<>(yearStrings.get(i), count[1]));     // Bar chart 2 Q2
                    seriesQtr3.getData().add(new XYChart.Data<>(yearStrings.get(i), count[2]));     // Bar chart 2 Q3
                    seriesQtr4.getData().add(new XYChart.Data<>(yearStrings.get(i), count[3]));     // Bar chart 2 Q4

                    slice1 = new PieChart.Data(yearStrings.get(i) + ": Q1", count[0]);
                    slice2 = new PieChart.Data(yearStrings.get(i) + ": Q2", count[1]);
                    slice3 = new PieChart.Data(yearStrings.get(i) + ": Q3", count[2]);
                    slice4 = new PieChart.Data(yearStrings.get(i) + ": Q4", count[3]);

                    pieChart2.getData().addAll(slice1, slice2, slice3, slice4);
                    
                    pieChart2.getData().stream().forEach(data -> 
                    {
                        Tooltip tooltip = new Tooltip();
                        tooltip.setText(data.getPieValue() + " Sales");
                        Tooltip.install(data.getNode(), tooltip);
                        data.pieValueProperty().addListener((observable, oldValue, newValue) -> 
                        tooltip.setText(newValue + " Sales"));
                    });

                }

                // Chart set 2
                // -------------
                barChart2.getData().addAll(seriesQtr1, seriesQtr2, seriesQtr3, seriesQtr4);
                
                // Bar Chart 3
                for (int i = 0; i < regionStrings.size(); i++)
                {                   
                    int count = 0;                   
                    
                    for (Vehicles vehicle : vehicles)
                    {
                        if (vehicle.getYearString().equals(checkBox.getText()))
                        {                                               
                            if (vehicle.getRegion().equals(regionStrings.get(i)))
                            {
                                count += vehicle.getQuantity();
                            }
                        }
                    }
                    if (count == 0)
                        continue;
                    
                    seriesRegion.getData().add(new XYChart.Data<>(regionStrings.get(i), count));     // Bar chart 3

                    pieChart3.getData().add(new PieChart.Data(regionStrings.get(i) + ": " + checkBox.getText(), count));

                }
                
                barChart3.getData().addAll(seriesRegion);
                
                pieChart3.getData().stream().forEach(data -> 
                {
                    Tooltip tooltip = new Tooltip();
                    tooltip.setText(data.getPieValue() + " Sales");
                    Tooltip.install(data.getNode(), tooltip);
                    data.pieValueProperty().addListener((observable, oldValue, newValue) -> 
                    tooltip.setText(newValue + " Sales"));
                });
            }
        }
    }

    //Provides functionality to radio buttons and allows the changing of the graph being viewed
    private void constructRadio()
    {
        myGroup1.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle)
            {
                if (myGroup1.getSelectedToggle().equals(radioBar1))
                {
                    barChart1.setVisible(true);
                    pieChart1.setVisible(false);
                } 
                else if (myGroup1.getSelectedToggle().equals(radioPie1))
                {
                    barChart1.setVisible(false);
                    pieChart1.setVisible(true);
                } 
            }
        });
        
        myGroup2.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle)
            {
                if (myGroup2.getSelectedToggle().equals(radioBar2))
                {
                    barChart2.setVisible(true);
                    pieChart2.setVisible(false);
                } 
                else if (myGroup2.getSelectedToggle().equals(radioPie2))
                {
                    barChart2.setVisible(false);
                    pieChart2.setVisible(true);
                } 
            }
        });
                
                
        myGroup3.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle)
            {
                if (myGroup3.getSelectedToggle().equals(radioBar3))
                {
                    barChart3.setVisible(true);
                    pieChart3.setVisible(false);
                } 
                else if (myGroup3.getSelectedToggle().equals(radioPie3))
                {
                    barChart3.setVisible(false);
                    pieChart3.setVisible(true);
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
}
