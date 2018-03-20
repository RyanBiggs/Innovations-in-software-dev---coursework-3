package cw3;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class DashboardController implements Initializable
{
    private static String markup;
    private static List<Vehicles> vehicles;
    private static List<String> yearStrings, vehicleStrings, regionStrings;
    private DashService service;
    
    // Delcare Checkboxes
    private CheckBox[] yearCheckBoxes;
    
    // Delclare radio buttons and toggle groups
    @FXML
    private RadioButton radioBar1, radioPie1, radioBar2, radioPie2, radioBar3, radioPie3;
    @FXML
    private ToggleGroup myGroup1, myGroup2, myGroup3;
    @FXML
    private HBox yearCheckBoxesContainer, totalSalesChartType, quarterlySalesChartType, regionalSalesChartType;
    @FXML
    private AnchorPane AnchorPane1;

    // Declare barcharts and axes
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
    
    // Declare piecharts
    @FXML
    private PieChart pieChart1;
    @FXML
    private PieChart pieChart2;
    @FXML
    private PieChart pieChart3;
    
    // Declare menubar contents
    @FXML
    private Button refresh = new Button();
    @FXML
    private Label dateTime;
    
    // Declare progress indicator
    @FXML
    private ProgressIndicator progressIndicator;  

    //******************************************
    //  initialize - Called upon initialisation
    //******************************************
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
        
        // Event handler for successful run of service
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() 
        {
            @Override
            public void handle(WorkerStateEvent e) 
            {
                markup = e.getSource().getValue().toString();

                Gson gson = new Gson();
                
                vehicles = gson.fromJson( markup, new TypeToken<ObservableList<Vehicles>>(){}.getType() );
                
// DEBUG
//                vehicles.add(new Vehicles("America", "Elise", 100, 2014, (byte)1)); // NOTE : Insert Data
//                vehicles.add(new Vehicles("America", "Elise", 145, 2014, (byte)2)); // NOTE : Insert Data
//                vehicles.add(new Vehicles("America", "Elise", 200, 2014, (byte)3)); // NOTE : Insert Data
//                vehicles.add(new Vehicles("America", "Elise", 800, 2014, (byte)4)); // NOTE : Insert Data

                // String of all distinct years in data source [i.e. "2011, 2012, 2013"]
                yearStrings = vehicles.stream()
                        .map(object -> object.getYearString())
                        .distinct()
                        .collect(Collectors.toList());
                
                // String of all distinct vehicles in data source [i.e. "Elise, Evora, Exige"]
                vehicleStrings = vehicles.stream()
                        .map(object -> object.getVehicle())
                        .distinct()
                        .collect(Collectors.toList());
                
                // String of all distinct regions in data source [i.e. "America, Asia, Europe"]
                regionStrings = vehicles.stream()
                        .map(object -> object.getRegion())
                        .distinct()
                        .collect(Collectors.toList());
                
// DEBUG
//                System.out.println("YearStrings" + yearStrings + "\n");
//                System.out.println("VehicleStrings" + vehicleStrings + "\n");
//                System.out.println("RegionStrings" + regionStrings + "\n");
//                System.out.println("VEHICLES : " + vehicles + "\n");
                
                // Clear the year selector checkboxes, so when refreshed duplicates aren't added
                yearCheckBoxesContainer.getChildren().clear();
                
                constructCheckBoxes();
                constructRadio();
                constructMenuBar();
                
                // Re-enable UI stuff once refresh is complete
                refresh.setDisable(false);
                totalSalesChartType.setDisable(false);
                quarterlySalesChartType.setDisable(false);
                regionalSalesChartType.setDisable(false);
                yearCheckBoxesContainer.setDisable(false);
                                
            }
        });

        // Start service
        service.start();
        
        // OnClick for refresh button
        refresh.setOnAction((ActionEvent e) -> 
        {
            System.out.println("Refreshing Data!");
            
            // Disable refresh button once clicked
            refresh.setDisable(true);
            
            // Disable other UI stuff           
            totalSalesChartType.setDisable(true);
            quarterlySalesChartType.setDisable(true);
            regionalSalesChartType.setDisable(true);
            yearCheckBoxesContainer.setDisable(true);
            
            // Clear graphs (so old data is removed)
            barChart1.getData().clear();
            pieChart1.getData().clear();
            barChart2.getData().clear();
            pieChart2.getData().clear();
            barChart3.getData().clear();
            pieChart3.getData().clear();
            
            // Restart service
            service.restart();
            
        });
    }

    //**************************************************************
    //  constructCheckBoxes - Constructs the year select checkboxes
    //**************************************************************
    private void constructCheckBoxes()
    {        
        // Create x amount of checkboxes, where x is the number of distinct years in data source
        yearCheckBoxes = new CheckBox[yearStrings.size()];
        
        //loops through check boxes
        for (byte index = 0; index < yearStrings.size(); index++)
        {
            yearCheckBoxes[index] = new CheckBox(yearStrings.get(index));
            
            // Checkboxes atart unselected
            yearCheckBoxes[index].setSelected(false);
            
            // Event filters
            yearCheckBoxes[index].addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent e) 
                {
                    System.out.println("Firstly, Event Filters !");
                }
            });
            
            // Event handlers
            yearCheckBoxes[index].addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent e)
                {
                    System.out.println("Secondly, Event Handlers !");
                }
            });
            
            // On action
            yearCheckBoxes[index].setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent e)
                {
                    System.out.println("Thirdly, Convenience Methods !");

                    constructSeries();
                }
            });
            
            //adds the constructed check boxes to the correct UI element (yearCheckBoxesContainer)
            yearCheckBoxesContainer.getChildren().add(yearCheckBoxes[index]);
        }
        // Size the main anchor pane to the window size
        AnchorPane1.getScene().getWindow().sizeToScene();
    }

    //*********************************************
    //  constructSeries - Functionality for graphs
    //*********************************************
    private void constructSeries()
    {
        // Clear all graphs of data
        barChart1.getData().clear();
        pieChart1.getData().clear();
        barChart2.getData().clear();
        pieChart2.getData().clear();
        barChart3.getData().clear();
        pieChart3.getData().clear();

        // For each checkbox the yearCheckBoxes array...
        for (CheckBox checkBox : yearCheckBoxes)
        {
            // if the checkbox is selected...
            if (checkBox.isSelected())
            {
                // Create new series for total sales bar chart,
                // and set the name to the selected checkbox text (selected year).
                XYChart.Series series = new XYChart.Series();
                series.setName(checkBox.getText());             

                // Create new series' for quarterly sales bar chart (one for each quarter),  
                // and set the name to the selected checkbox text (selected year),
                // plus "Q1, Q2, Q3 & Q4" for each quarter respectively.
                XYChart.Series seriesQtr1 = new XYChart.Series();
                seriesQtr1.setName(checkBox.getText() + ": Q1");
                
                
                XYChart.Series seriesQtr2 = new XYChart.Series();
                seriesQtr2.setName(checkBox.getText() + ": Q2");
                
                XYChart.Series seriesQtr3 = new XYChart.Series();
                seriesQtr3.setName(checkBox.getText() + ": Q3");
                
                XYChart.Series seriesQtr4 = new XYChart.Series();
                seriesQtr4.setName(checkBox.getText() + ": Q4");
                
                // Create a slice for each quarter in the quarterly sales pie chart.
                PieChart.Data slice1 = null;
                PieChart.Data slice2 = null;
                PieChart.Data slice3 = null;
                PieChart.Data slice4 = null;
                
                // Create new series for  regional sales bar chart,
                // and set the name to the selected checkbox text (selected year).
                XYChart.Series seriesRegion = new XYChart.Series();
                seriesRegion.setName(checkBox.getText());
                  
                // Code handling yearly sales set of charts
                // ========================================
                
                // for each distinct vehicle...
                for (int i = 0; i < vehicleStrings.size(); i++)
                {                   
                    int totalSales = 0;
                    
                    // for each vehicle in the vehicles list...
                    for (Vehicles vehicle : vehicles)
                    {
                        // if the vehicle matches one from the data source...
                        if( vehicleStrings.get(i).equals(vehicle.getVehicle()) )
                        {
                            // And if the year is equal to the selected year...
                            if (vehicle.getYearString().equals(checkBox.getText()))
                            {  
                                // Total up the sales of all the vehicles
                                totalSales += vehicle.getQuantity();
                                
                                // Clarification:
                                // If the selected year is 2011, this will total up the sales of each distinct vehicle of that year.
                                // I.e. Elise = 699, Evora = 555, Exige = 316.
                            }
                        }
                    }
                    
                    // Adds the data to the bar chart series. The vehicle name on the X axis, and the total sales of said vehicle on the Y.
                    series.getData().add(new XYChart.Data<>(vehicleStrings.get(i), totalSales));
                             
                    // Adds the data to the pie chart. The vehicle name + the selected year as the slices, and the total sales as the size of said slices.
                    pieChart1.getData().add(new PieChart.Data(vehicleStrings.get(i) + ": " + checkBox.getText(), totalSales)); 
                    
                    // Clarification:
                    // This is done here, rather than where the bar chart is done below, because otherwise, all years are added to the chart, and those that are unselected have a value of zero.
                    // Adding the data here only adds the selected year to the chart.
                } 
                
                // Adds the bar chart series to the bar chart
                barChart1.getData().add(series); 
                
                // Tooltip for pie chart, hovering the cursor over each slice will display the number of sales for that slice.
                pieChart1.getData().stream().forEach(data -> 
                {
                    Tooltip tooltip = new Tooltip();
                    tooltip.setText(data.getPieValue() + " Sales");
                    Tooltip.install(data.getNode(), tooltip);
                    data.pieValueProperty().addListener((observable, oldValue, newValue) -> 
                    tooltip.setText(newValue + " Sales"));
                });
                
                // Code handling quaterly sales set of charts
                // ==========================================
                
                // for each distinct year...
                for (int i = 0; i < yearStrings.size(); i++)
                {                   
                    int[] totalSales = {0,0,0,0};    // A total for each quarter.                   
                    
                    // for each vehicle in the vehicles list...
                    for (Vehicles vehicle : vehicles)
                    {
                        // if the year is equal to the selected year...
                        if (vehicle.getYearString().equals(checkBox.getText()))
                        {        
                            // And if the year matches one from the data source...
                            if (vehicle.getYearString().equals(yearStrings.get(i)))
                            {
                                // Total up the sales of all the vehicles for each quarter
                                totalSales[vehicle.getQTR() - 1] += vehicle.getQuantity();
                                
                                // Clarification:
                                // If the selected year is 2011, this will total up the sales of each distinct vehicle of each quarter of that year.
                                // I.e. 
                                //      Q1: Elise = 157, Evora = 75, Exige = 78.
                                //      Q2: Elise = 167, Evora = 183, Exige = 72.
                                //      Q3: Elise = 217, Evora = 175, Exige = 87.
                                //      Q4: Elise = 158, Evora = 122, Exige = 79.
                            }
                        }
                    }
                    
                    // if the total sales for all quarters is 0, continue.
                    if (totalSales[0] == 0 && totalSales[1] == 0 && totalSales[2] == 0 && totalSales[3] == 0)
                        continue;
                    
                    // Adds the data to the bar chart series. The year on the X axis, and the total sales of each quarter on the Y.
                    seriesQtr1.getData().add(new XYChart.Data<>(yearStrings.get(i), totalSales[0]));     // Q1
                    seriesQtr2.getData().add(new XYChart.Data<>(yearStrings.get(i), totalSales[1]));     // Q2
                    seriesQtr3.getData().add(new XYChart.Data<>(yearStrings.get(i), totalSales[2]));     // Q3
                    seriesQtr4.getData().add(new XYChart.Data<>(yearStrings.get(i), totalSales[3]));     // Q4

                    // Adds the data to the pie chart slices. The year + the Q1, Q2, Q3, Q4 for each quater respectively as the slices, 
                    // and the total sales of each quarter as the size of said slices.
                    slice1 = new PieChart.Data(yearStrings.get(i) + ": Q1", totalSales[0]);
                    slice2 = new PieChart.Data(yearStrings.get(i) + ": Q2", totalSales[1]);
                    slice3 = new PieChart.Data(yearStrings.get(i) + ": Q3", totalSales[2]);
                    slice4 = new PieChart.Data(yearStrings.get(i) + ": Q4", totalSales[3]);

                    // Adds all four slices to the pie chart
                    pieChart2.getData().addAll(slice1, slice2, slice3, slice4);
                    
                    // Tooltip for pie chart, hovering the cursor over each slice will display the number of sales for that slice.
                    pieChart2.getData().stream().forEach(data -> 
                    {
                        Tooltip tooltip = new Tooltip();
                        tooltip.setText(data.getPieValue() + " Sales");
                        Tooltip.install(data.getNode(), tooltip);
                        data.pieValueProperty().addListener((observable, oldValue, newValue) -> 
                        tooltip.setText(newValue + " Sales"));
                    });

                }

                // Adds the bar chart series' to bar chart.
                barChart2.getData().addAll(seriesQtr1, seriesQtr2, seriesQtr3, seriesQtr4);
                
                // Code handling regional sales set of charts
                // ==========================================
                
                // for each distinct region...
                for (int i = 0; i < regionStrings.size(); i++)
                {                   
                    int totalSales = 0;                   
                    
                    // for each vehicle in the vehicles list...
                    for (Vehicles vehicle : vehicles)
                    {
                        // if the year is equal to the selected year...
                        if (vehicle.getYearString().equals(checkBox.getText()))
                        {         
                            // And the region matches one from the data souce...
                            if (vehicle.getRegion().equals(regionStrings.get(i)))
                            {
                                // Total up the sales of all the vehicles.
                                totalSales += vehicle.getQuantity();
                                
                                // Clarification:
                                // If the selected year is 2011, this will total up the sales of all vehicles that sold in that region.
                                // I.e. 
                                //      America: 179.
                                //      Asia: 669.
                                //      Europe: 722.
                            }
                        }
                    }
                    
                    // if the total sales is 0, continue.
                    if (totalSales == 0)
                        continue;
                    
                    // Adds the data to the bar chart series. The region on the X axis, and the total sales of each region on the Y.
                    seriesRegion.getData().add(new XYChart.Data<>(regionStrings.get(i), totalSales));     // Bar chart 3

                    // Adds the data to the pie chart. The region name + the selected year as the slices, and the total sales as the size of said slices.
                    pieChart3.getData().add(new PieChart.Data(regionStrings.get(i) + ": " + checkBox.getText(), totalSales));
                }
                
                // Adds the bar chart series' to bar chart.
                barChart3.getData().addAll(seriesRegion);
                
                // Tooltip for pie chart, hovering the cursor over each slice will display the number of sales for that slice.
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

    //*****************************************************************************
    //  constructRadio - Provides functionality for the graph select radio buttons
    //*****************************************************************************
    private void constructRadio()
    {
        // Group of radio buttons for the total sales charts
        myGroup1.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle)
            {
                // if the 'Bar Chart' radio button is ticked...
                if (myGroup1.getSelectedToggle().equals(radioBar1))
                {
                    // Show bar chart and hide pie chart.
                    barChart1.setVisible(true);
                    pieChart1.setVisible(false);
                } 
                
                // if the 'Pie Chart' radio button is ticked...
                else if (myGroup1.getSelectedToggle().equals(radioPie1))
                {
                    // Show pie chart and hide bar chart.                    
                    barChart1.setVisible(false);
                    pieChart1.setVisible(true);
                } 
            }
        });
        
        // Group of radio buttons for the quaterly sales charts        
        myGroup2.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle)
            {
                // if the 'Bar Chart' radio button is ticked...
                if (myGroup2.getSelectedToggle().equals(radioBar2))
                {
                    // Show bar chart and hide pie chart.
                    barChart2.setVisible(true);
                    pieChart2.setVisible(false);
                } 
                
                // if the 'Pie Chart' radio button is ticked...
                else if (myGroup2.getSelectedToggle().equals(radioPie2))
                {
                    // Show pie chart and hide bar chart. 
                    barChart2.setVisible(false);
                    pieChart2.setVisible(true);
                } 
            }
        });
                
        // Group of radio buttons for the regional sales charts        
        myGroup3.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle)
            {
                // if the 'Bar Chart' radio button is ticked...
                if (myGroup3.getSelectedToggle().equals(radioBar3))
                {
                    // Show bar chart and hide pie chart.
                    barChart3.setVisible(true);
                    pieChart3.setVisible(false);
                } 
                
                // if the 'Pie Chart' radio button is ticked...
                else if (myGroup3.getSelectedToggle().equals(radioPie3))
                {
                    // Show pie chart and hide bar chart.
                    barChart3.setVisible(false);
                    pieChart3.setVisible(true);
                } 
            }
        });        
    }

    //************************************************************************
    //  constructSeries - Handles functionality for connecting to data source
    //************************************************************************
    private static class DashService extends Service<String>
    {
        // New string property to store address
        private StringProperty address = new SimpleStringProperty();

        // Sets the address to passed in string
        public final void setAddress(String address)
        {
            this.address.set(address);
        }

        // Returns the address as a string
        public final String getAddress()
        {
            return address.get();
        }

        // Returns the address as a string property
        public final StringProperty addressProperty()
        {
            return address;
        }

        // Handles connectivity to the data source
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
                        // Try to set up connection to the given url, and request data.
                        url = new URL(getAddress());
                        connect = (HttpURLConnection) url.openConnection();
                        connect.setRequestMethod("GET");
                        connect.setRequestProperty("Accept", "application/json");
                        connect.setRequestProperty("Content-Type", "application/json");

                        // Reads the data source and assigns it to a string
                        markup = (new BufferedReader(new InputStreamReader(connect.getInputStream()))).readLine();
                    } 
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        // Disconnect
                        if (connect != null)
                            connect.disconnect();
                    }
                    
                    // Retuns the data from the online data source
                    return markup;
                }
            };
        }
    }
    
    //*********************************************************
    //  constructMenuBar - Provides functionality for menu bar
    //*********************************************************
    private void constructMenuBar()
    {        
        // Date / time display
        dateTime();
        
        // Add a print to file button?
//        printData();

        // Add export data to png button?
//        exportData();
    }
    
    //**********************************************************
    //  dateTime - Fetches local date and time, and displays it
    //**********************************************************
    private void dateTime()
    {
        Task Task1 = new Task<Void>() 
        {
            @Override
            public Void call() throws InterruptedException 
            {
                while (true) 
                {
                    // Break operation if cancelled
                    if (isCancelled()) 
                        break;

                    // Get the local date time formatted as "d MMM yyyy hh:mm:ss" ( 17 Mar 2018 15:06:25 )
                    this.updateMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm:ss"))); 
                }

                return null;
            }
        };
        
        Task1.messageProperty().addListener(new ChangeListener() 
        {
            @Override
            public void changed(ObservableValue o, Object ov, Object nv) 
            {
                // Set the text of the dateTime label to the above formatted data/time.
                dateTime.setText((String) o.getValue());
            }
        });

        // Start the clock on a separate thread
        new Thread(Task1).start();
    }
}
