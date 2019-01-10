package Medidores;

import Model.MedicaoPulsoCardiaco;
import com.fazecast.jSerialComm.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class Oximetro {

    private final String complatibleModel = "oximetro";

    private MedicaoPulsoCardiaco med;

    private Scanner in;
    private OutputStream out;

    private String deviceCOM;
    private String mp;
    private String model;
    private String portocol;

    private boolean feedback;
    private String ans;

    /**
     * @return the deviceCOM
     */
    public String getDeviceCOM() {
        return deviceCOM;
    }

    /**
     * @return boolean
     */
    public boolean setDeviceCOM() {
        //procura um equipamento compativel
        return true;
    }

    /**
     * @return the mp
     */
    public String getMp() {
        return mp;
    }

    /**
     * @return boolean
     */
    public boolean firstMessage() throws IOException {
        String resp = "";
        boolean cont = true;

        //procura um equipamento compativel
        SerialPort ports[] = SerialPort.getCommPorts();
        SerialPort serialPort;

        for (SerialPort port : ports) {
            //Seleciona uma porta
            serialPort = port;

            if (!serialPort.openPort()) {
                System.out.println("erro a abrir a porta");
                return false;
            }

            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0); //Muda os timeouts
            in = new Scanner(serialPort.getInputStream());
            out = serialPort.getOutputStream();

            //Envia o comando 1
            out.write(1);
            System.out.println("enviei");

            //Recebe a resposta
            while (in.hasNextLine() && cont) {
                resp = in.nextLine();
                if (resp.contains(this.complatibleModel)) {
                    cont = false;
                    break;
                }
            }
            
            
            System.out.println("Recebi resposta");
            break;
        }
        
        
        return true;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     *
     */
    public void setModel() {
        int p = this.mp.indexOf(";");
        this.portocol = this.mp.substring(0, p);
    }

    /**
     * @return the portocol
     */
    public String getPortocol() {
        return portocol;
    }

    /**
     *
     */
    public void setPortocol() {
        int p = this.mp.indexOf(";");
        this.portocol = this.mp.substring(p);
    }

    /**
     * @return the feedback
     */
    public boolean getFeedback() {
        return feedback;
    }

    /**
     * @param feedback the feedback to set
     */
    public void setFeedback(boolean feedback) {
        this.feedback = feedback;
    }

    /**
     * @return the med
     */
    public MedicaoPulsoCardiaco getMed() {
        return med;
    }

    /**
     * @param med the med to set
     */
    public void setMed(MedicaoPulsoCardiaco med) {
        this.med = med;
    }

    public boolean sendCommand(int c) throws IOException {  //Comando com respota
        boolean cont = true;
        String resp = "";

        out.write(c);

        while (in.hasNextLine()) {
            resp = in.nextLine();
        }

        try {
            this.ans = "" + Integer.parseInt(resp);
            this.feedback = true;
        } catch (NumberFormatException e) {
            this.feedback = false;
        }

        return this.feedback;
    }

    /*public boolean firstMessage() throws IOException {
        boolean fb1, fb2;

        fb1 = setDeviceCOM();
        fb2 = setMp();

        if (fb1 == true && fb2 == true) {
            setModel();
            setPortocol();
            return true;
        } else {
            return false;
        }
    }*/
    public boolean startMeasure() throws IOException {

        sendCommand(3);

        if (this.feedback == true) {

            try {
                getMed().setPulsoMedio(Integer.parseInt(this.ans));
            } catch (NumberFormatException e) {
                getMed().setError(true);
            }
            getMed().setTimestamp();
            return true;
        } else {
            getMed().setError(true);
            return false;
        }

    }

}
