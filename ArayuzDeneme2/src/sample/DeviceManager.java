package sample;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.ArrayList;
import java.util.List;

public class DeviceManager {
    private String deviceDescription;
    private String deviceName;
    private PcapIf device;
    private int deviceCount;
    private List<PcapIf> alldevs = new ArrayList<PcapIf>();
    public int getDeviceCount() {
        return this.deviceCount;
    }
    public void scanDevices(){
        StringBuilder errbuf = new StringBuilder(); // herhangi bir hata mesajı için
        int r = Pcap.findAllDevs(alldevs,errbuf);
        if(r == -1 || alldevs.isEmpty()){
            System.err.printf("Aygıt listesi okunamadı, hata -> %s",errbuf.toString());
            return;
        }
        System.out.println("Ağ aygıtları : ");

        int i=0;
        for (PcapIf device:alldevs){
            this.deviceDescription = (device.getDescription() != null) ? device.getDescription() : "Açıklama yok";
            if(this.deviceDescription.equals("")) this.deviceDescription="Unknown";
            System.out.printf("#%d: %s [%s]\n",++i, device.getName(),this.deviceDescription);
            this.deviceCount=i;
        }
        System.out.println(this.deviceCount);
        System.out.println(alldevs.size());
    }
    public void selectDevice(int index){
        this.device=alldevs.get(index);
    }
    public String getDeviceDescription() {
        return deviceDescription;
    }
    public String getDeviceName() {
        return deviceName;
    }

    public PcapIf getDevice() {
        return device;
    }

    public void setDevice(PcapIf device) {
        this.device = device;
    }

    public List<PcapIf> getAlldevs() {
        return alldevs;
    }

}
