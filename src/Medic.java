import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Medic extends  JFrame {
    private JTabbedPane General;
    private JTextField NumeAng;
    private JTextField PrenumeAng;
    private JTextField medicTextField;
    private JLabel UsernameLS;
    private JTextField CNPCautareIstoricTF;
    private JButton CautaPacientIstoric;
    private JLabel UsernameVLM;
    private JPanel Medic;
    private JButton CautaRaport;
    private JTextField DataProgramariiTF;
    private JTextField ProgramariTF;
    private JTextField CNPCautareRaport;
    private JTextField DataCautareRaport;
    private JTextField DenumireServiciu;
    private JTextField PretServiciu;
    private JTextField DurataServiciu;
    private JComboBox TipServiciu;
    private JTextPane AfisareSalarii;
    private JTextPane OrarAfisare;
    private JButton AdaugareServiciu;
    private JButton StergereServiciu;
    private JButton AdaugaRaport;

    public Medic() {

        setTitle("Medical");
        setContentPane(Medic);
        setSize(500, 550);
        //setLocationRelativeTo();
        /// CNPTextField.setMaximumSize(100,100);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

    }
    public static void main(String[] args) {
        // Assuming you pass the logged-in username from the LoginForm

        new Medic();
    }
}