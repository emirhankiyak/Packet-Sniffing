package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import java.nio.charset.StandardCharsets;

public class Packet {
    private int packetNo;
    private String sourceIp;
    private String destinationIp;
    private String protocol;
    private int packetLength;
    private String sourcePort;
    private String destinationPort;
    private PcapIf currentDevice;
    private String payload;
    private Pcap pcap;

    public Packet(int packetNo, String sourceIp, String destinationIp, String protocol, int packetLength, String sourcePort, String destinationPort) {
        this.packetNo = packetNo;
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.protocol = protocol;
        this.packetLength = packetLength;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }
    public Packet(){

    }
    public void stopCapture(){
        this.pcap.close();
    }
    private Ip4 ip = new Ip4 ();
    private Icmp icmp = new Icmp ();
    private Tcp tcp = new Tcp ();
    private Udp udp = new Udp ();
    private Http http = new Http();
    private Arp arp = new Arp();
    public ObservableList<Packet> packetList = FXCollections.observableArrayList();

    public void handlePacket(PcapPacket packet){
        if(packet.hasHeader(ip)) handleIP(packet);
        if(packet.hasHeader(tcp)) handleTCP(packet);
        if(packet.hasHeader(udp)) handleUDP(packet);
        if(packet.hasHeader(icmp)) handleICMP(packet);
        if(packet.hasHeader(arp)) handleARP(packet);
        this.packetList.add(new Packet(this.packetNo,this.sourceIp,this.destinationIp,this.protocol,this.packetLength,this.sourcePort,this.destinationPort));
    }
    private void handleIP (PcapPacket packet){
        packet.getHeader(ip);
        byte[] sIP = new byte[4];
        byte[] dIP = new byte[4];
        sIP = ip.source();
        dIP = ip.destination();
        this.sourceIp = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
        this.destinationIp = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
        this.packetLength = packet.getCaptureHeader().caplen();
        System.out.println("Kaynak IP : " + this.sourceIp +
                " || Hedef IP : " + this.destinationIp +
                " || Paket boyutu : " + this.packetLength);
    }
    private void handleTCP (PcapPacket packet){
        packet.getHeader(tcp);
        byte[] payloadArray = new byte[1000];
        //String payload = new String(payloadArray, StandardCharsets.UTF_8);
        this.sourcePort = String.valueOf(tcp.source());
        this.destinationPort = String.valueOf(tcp.destination());
        if(packet.hasHeader(http)){
            this.protocol = "TCP / HTTP";
            System.out.println("Protokol : " + "TCP / HTTP" +
                    " || Kaynak port : " + this.sourcePort +
                    " || Hedef Port : " + this.destinationPort
            );
        }else{
            this.protocol = "TCP";
            System.out.println("Protokol : " + "TCP" +
                    " || Kaynak port : " + this.sourcePort +
                    " || Hedef Port : " + this.destinationPort
                    //" || Payload : " + payload
            );
        }

    }
    private void handleUDP(PcapPacket packet){
        packet.getHeader(udp);
        this.sourcePort = String.valueOf(udp.source());
        this.destinationPort = String.valueOf(udp.destination());
        this.protocol = "UDP";
        System.out.println("Protokol : " + "UDP" +
                " || Kaynak port : " + this.sourcePort +
                " || Hedef Port : " + this.destinationPort
        );
    }
    private void handleICMP(PcapPacket packet){
        packet.getHeader(icmp);
        System.out.println(" Protokol : ICMP");
    }
    private void handleARP(PcapPacket packet){
        packet.getHeader(arp);
        System.out.println("ARP yakalandı");
    }
    public void startCapture(int index, DeviceManager deviceManager){
        StringBuilder errbuf = new StringBuilder();
        deviceManager.selectDevice(index);
        System.out.printf("\n'%s' aygıtı için yakalanıyor:\n", (deviceManager.getDevice().getDescription() != null) ? deviceManager.getDevice().getDescription() : deviceManager.getDevice().getName());
        int snaplen = 64*1024; // kesme olmadan tüm paketleri yakala
        int flags = Pcap.MODE_PROMISCUOUS; // tüm paketleri yakala
        int timeout = 2*1000;
        this.pcap = Pcap.openLive(deviceManager.getDevice().getName(),snaplen,flags,timeout,errbuf);
        if(pcap == null){
            System.err.printf("Paket yakalama için ağ aygıtı açılırken hata oluştu : " + errbuf.toString());
            return;
        }
        PcapPacketHandler<String> jPacketHandler = new PcapPacketHandler<String>() {
            int packetCounter=1;
            @Override
            public void nextPacket(PcapPacket pcapPacket, String s) {
                byte[] data = pcapPacket.getByteArray(0,pcapPacket.size());
                System.out.printf("Paket No : %d\n",packetCounter++);
                ++packetNo;
                handlePacket(pcapPacket);
                System.out.println("--------------------------------------------------------------------------------");
            }
        };
        pcap.loop(30,jPacketHandler,"jNetPcap");
    }

    public PcapPacket currentPacket(PcapPacket packet){
        return packet;
    }

    // GETTER AND SETTERS
    public int getPacketNo() {
        return packetNo;
    }

    public void setPacketNo(int packetNo) {
        this.packetNo = packetNo;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public void setDestinationIp(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPacketLength() {
        return packetLength;
    }

    public void setPacketLength(int packetLength) {
        this.packetLength = packetLength;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }
}
