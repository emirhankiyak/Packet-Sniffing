package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jnetpcap.PcapIf;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    int selectedIndex=0;
    @FXML public TableView myTable;
    @FXML public TableColumn<Packet,Integer> packetNo;
    @FXML public TableColumn<Packet,String> sourceIp;
    @FXML public TableColumn<Packet,String> destinationIp;
    @FXML public TableColumn<Packet,String> protocol;
    @FXML public TableColumn<Packet,String> sourcePort;
    @FXML public TableColumn<Packet,String> destinationPort;
    @FXML public TableColumn<Packet,Integer> packetLength;


    @FXML public Button startButton;
    @FXML public Button stopButton;
    @FXML public ChoiceBox<String> agAygitlari;
    @FXML public Label deviceLabel;

    public void setTableValues(){
        packetNo.setCellValueFactory(new PropertyValueFactory<Packet,Integer>("packetNo"));
        sourceIp.setCellValueFactory(new PropertyValueFactory<Packet,String>("sourceIp"));
        destinationIp.setCellValueFactory(new PropertyValueFactory<Packet,String>("destinationIp"));
        protocol.setCellValueFactory(new PropertyValueFactory<Packet,String>("protocol"));
        sourcePort.setCellValueFactory(new PropertyValueFactory<Packet,String>("sourcePort"));
        destinationPort.setCellValueFactory(new PropertyValueFactory<Packet,String>("destinationPort"));
        packetLength.setCellValueFactory(new PropertyValueFactory<Packet,Integer>("packetLength"));
        myTable.setItems(packet.packetList);
    }
    private Packet packet;
    private DeviceManager device;
    public void basla(ActionEvent event){
        packet = new Packet();
        packet.startCapture(selectedIndex,device);
        setTableValues();
        startButton.setDisable(true);
        stopButton.setDisable(false);
    }
    public void bitir(ActionEvent event){
        packet.stopCapture();
        stopButton.setDisable(true);
        startButton.setDisable(false);
    }
    public void aygitSecim(){
        System.out.println("Tıklandı");
        agAygitlari.setOnAction((actionEvent -> {
            selectedIndex = agAygitlari.getSelectionModel().getSelectedIndex();
            device.selectDevice(selectedIndex);
            deviceLabel.setText(device.getDevice().getDescription());
            startButton.setDisable(false);
        }));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deviceLabel.setDisable(true);
        device = new DeviceManager();
        device.scanDevices();
        for(int i=0; i<device.getDeviceCount(); i++){
            agAygitlari.getItems().add(device.getAlldevs().get(i).getName() + " ["+device.getAlldevs().get(i).getDescription()+"]");
        }
    }
}
