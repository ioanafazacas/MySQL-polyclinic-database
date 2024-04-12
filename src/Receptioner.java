import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Receptioner extends JFrame{
    private JTabbedPane receptiePanel;
    public int id_angajat;
    public  String username;
    public  JTextField NumeAng;
    public  JTextField PrenumeAng;
    private JPanel InregistrarePanel;
    private JPanel ProgramarePanel;
    private JPanel BonPanel;
    private JTextField textFieldfunctieR;
    private JTextField textFieldCNPinregistrare;
    private JLabel CNPinregistrareLabel;
    private JButton inregistrareButton;
    private JComboBox comboBoxServicii;
    private JLabel serviciuLabel;
    private JLabel dataLabel;
    private JTextField textFieldData;
    private JTextField textFieldPret;
    private JLabel pretLabel;
    private JButton programeazaButton;
    private JLabel CNPPacientLabel;
    private JTextField textFieldPacinetCNP;
    private JTextArea textAreaSalariu;
    private JTextArea textAreaConcedii;
    private JTextArea textAreaOrar;
    private JTextField textFieldNumePacinet;
    private JTextField textFieldprenumePacient;
    private JTextField textFieldBonCNP;
    private JTextField textFieldBonData;
    private JButton emiteBonButton;
    private JTextField receptionerTextField;
    private JButton logOutButton;
    private JButton vizualizareSalariiButton;
    private JButton vizualizareOrarButton;
    private JButton vizualizareConcediiButton;
    private JTextField textFieldOra;

    public Receptioner()
    {
        setTitle("Receptioner");
        setContentPane(receptiePanel);
        setSize(450, 500);
        //setLocationRelativeTo();

        String[] servicii={"ecografie",
                "endoscopie_digitala",
                "ecocardiografie",
                "cardiologie_interventionala",
                "bronhoscopie",
                "EEG",
                "EMG",
                "dializa",
                "chirurgie_laparoscopica",
                "chirurgie_toracica",
                "chirurgie_spinala",
                "CT"};
        comboBoxServicii.addItem("ecografie");
        comboBoxServicii.addItem("endoscopie_digitala");
        comboBoxServicii.addItem("ecocardiografie");
        comboBoxServicii.addItem("cardiologie_interventionala");
        comboBoxServicii.addItem("bronhoscopie");
        comboBoxServicii.addItem("EEG");
        comboBoxServicii.addItem("EMG");
        comboBoxServicii.addItem("dializa");
        comboBoxServicii.addItem("chirurgie_laparoscopica");
        comboBoxServicii.addItem("chirurgie_toracica");
        comboBoxServicii.addItem("chirurgie_spinala");
        comboBoxServicii.addItem("CT");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);



        vizualizareSalariiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afiseazaSalariiMedic();
            }
        });

        vizualizareConcediiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afiseazaConcediiMedic();
            }
        });

        vizualizareOrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afiseazaOrarMedic();
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Închide fereastra actuală
                dispose();

                // Deschide fereastra LoginForm (asigurați-vă că aveți o clasă LoginForm)
                LoginForm2 loginForm2 = new LoginForm2();
                loginForm2.setVisible(true);
            }
        });

        emiteBonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                afiseazaServiciiInTabel(textFieldBonData.getText(), textFieldBonCNP.getText());
            }
        });

        programeazaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get values from text fields and combo box
                String CNPp= textFieldPacinetCNP.getText();
                int idServicii = comboBoxServicii.getSelectedIndex() + 1; // Assuming the index starts from 0
                String dataProgramare = textFieldData.getText();
                int pretMedic = Integer.parseInt(textFieldPret.getText());
                String oraProgramare= textFieldOra.getText();

                try {
                    // Establish a database connection
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");

                    // Prepare the call to the stored procedure
                    CallableStatement cstmt = connection.prepareCall("{call programare2(?, ?, ?, ?, ?)}");
                    cstmt.setInt(1, idServicii);
                    cstmt.setDate(2, Date.valueOf(dataProgramare));
                    cstmt.setInt(3, pretMedic);
                    cstmt.setString(4, CNPp);
                    cstmt.setString(5,oraProgramare);

                    // Execute the stored procedure
                    cstmt.execute();

                    // Close the connection
                    connection.close();

                    JOptionPane.showMessageDialog(null, "Programare realizata cu succes!");

                    // Clear text fields
                    textFieldData.setText("");
                    textFieldPret.setText("");
                    textFieldPacinetCNP.setText("");
                    textFieldOra.setText("");

                } catch (SQLException | NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //inregistrarea unui pacient
        inregistrareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Preia CNP-ul introdus în textField
                String cnpPacient = textFieldCNPinregistrare.getText();

                // Verifică dacă CNP-ul este valid (poți adăuga mai multe verificări aici)
                if (cnpPacient.length() == 14) {
                    insertPacientIntoDatabase(cnpPacient);

                    JOptionPane.showMessageDialog(null, "Pacient înregistrat cu succes!");
                } else {
                    // Mesaj de eroare pentru CNP invalid
                    JOptionPane.showMessageDialog(null, "CNP invalid. Introduceți un CNP valid.");
                }
            }
        });
    }

    private void afiseazaOrarMedic() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Unitate");
        model.addColumn("Ora început");
        model.addColumn("Ora sfârșit");

        // Interogare pentru a obține programul medicului curent pe unitate
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123")) {
            String query = "SELECT pa.*, u.denumire FROM program_angajat_unitate pa, unitate_medicala u WHERE pa.id_unitate = u.id AND pa.id_angajat = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_angajat);  // Folosim id-ul medicului curent

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Adăugare rânduri la modelul de tabel bazat pe rezultatele interogării
                    while (resultSet.next()) {
                        String unitate = resultSet.getString("denumire");
                        String oraInceput = resultSet.getString("timp_start");
                        String oraSfarsit = resultSet.getString("timp_finish");

                        model.addRow(new Object[]{unitate, oraInceput, oraSfarsit});
                    }

                    // Afișare model de tabel într-o fereastră nouă
                    afisareOrarInTabel(model);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la afișarea orarului: " + ex.getMessage());
        }
    }

    private void afiseazaConcediiMedic() {
        // Creare model de tabel pentru stocarea informațiilor despre concedii
        DefaultTableModel modelConcedii = new DefaultTableModel();
        modelConcedii.addColumn("Data început");
        modelConcedii.addColumn("Data sfârșit");

        // Interogare pentru a obține concediile medicului curent
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123")) {
            String query = "SELECT * FROM concedii WHERE id_angajat = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_angajat);  // Folosim id-ul medicului curent

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Adăugare rânduri la modelul de tabel bazat pe rezultatele interogării
                    while (resultSet.next()) {
                        String dataInceput = resultSet.getString("data_incepere");
                        String dataSfarsit = resultSet.getString("data_finalizare");

                        modelConcedii.addRow(new Object[]{dataInceput, dataSfarsit});
                    }

                    // Afișare model de tabel într-o fereastră nouă
                    afisareConcediiInTabel(modelConcedii);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la afișarea concediilor: " + ex.getMessage());
        }
    }

    private void afisareConcediiInTabel(DefaultTableModel model) {
        // Creare fereastră pentru afișarea concediilor într-un tabel
        JFrame concediiFrame = new JFrame("Concedii Receptioner");
        concediiFrame.setSize(600, 400);

        // Creare tabel cu modelul specificat
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Adăugare tabel în fereastră
        concediiFrame.add(scrollPane);

        // Afișare fereastră
        concediiFrame.setVisible(true);
    }

    private void afisareOrarInTabel(DefaultTableModel model) {
        // Creare fereastră pentru afișarea orarului într-un tabel
        JFrame orarFrame = new JFrame("Orar Receptioner");
        orarFrame.setSize(600, 400);

        // Creare tabel cu modelul specificat
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Adăugare tabel în fereastră
        orarFrame.add(scrollPane);

        // Afișare fereastră
        orarFrame.setVisible(true);
    }

    private void afiseazaSalariiMedic() {
        // Creare fereastră pentru afișarea salariilor
        JFrame salariiFrame = new JFrame("Salarii Receptioner");
        salariiFrame.setSize(500, 300);

        // Creare panou pentru a adăuga componente
        JPanel salariiPanel = new JPanel();
        salariiFrame.add(salariiPanel);

        // Creare model de tabel pentru a afișa salariile într-un JTable
        DefaultTableModel modelSalarii = new DefaultTableModel();
        JTable tableSalarii = new JTable(modelSalarii);
        salariiPanel.add(new JScrollPane(tableSalarii));

        // Adăugare coloane la modelul de tabel
        modelSalarii.addColumn("An");
        modelSalarii.addColumn("Luna");
        modelSalarii.addColumn("Suma");

        // Apelare procedură stocată pentru a obține salariile medicului curent
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123")) {
            String callProcedure = "{CALL afisari_salarii_angajat(?)}";
            try (CallableStatement cstmt = connection.prepareCall(callProcedure)) {
                cstmt.setInt(1, id_angajat);  // Folosim id-ul medicului curent

                ResultSet resultSet = cstmt.executeQuery();

                // Adăugare rânduri la modelul de tabel bazat pe rezultatele procedurii stocate
                while (resultSet.next()) {
                    Object[] row = new Object[]{
                            resultSet.getInt("an"),
                            resultSet.getInt("luna"),
                            resultSet.getDouble("suma")
                    };
                    modelSalarii.addRow(row);
                }

                // Afișare fereastră
                salariiFrame.setVisible(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



    private String getDenumireUnitateMedicala(int idUnitate) {
        String denumireUnitate = "";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123")) {
            String sql = "SELECT denumire FROM unitate_medicala WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idUnitate);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        denumireUnitate = resultSet.getString("denumire");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return denumireUnitate;
    }

    private void afiseazaServiciiInTabel(String dataConsultatie, String cnpPacient) {
        try {
            // Conectare la baza de date
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");

            // Interogare SQL pentru a obține serviciile medicale și prețurile lor
            String query = "SELECT s.nume_serviciu, s.pret FROM servicii s " +
                    "INNER JOIN investigatii i ON s.id = i.id_servicii " +
                    "INNER JOIN raport_medical rm ON i.id_raport = rm.id_investigatii " +
                    "INNER JOIN pacient p ON rm.id_pacient = p.id_pacient " +
                    "WHERE rm.data_consult = ? AND p.CNP = ?";

            // Prepararea instrucțiunii SQL
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                // Setarea valorilor parametrilor
                preparedStatement.setString(1, dataConsultatie);
                preparedStatement.setString(2, cnpPacient);

                // Executarea interogării SQL
                ResultSet resultSet = preparedStatement.executeQuery();

                // Construirea modelului tabelului
                DefaultTableModel modelTabel = new DefaultTableModel();
                modelTabel.addColumn("Serviciu");
                modelTabel.addColumn("Pret");

                int totalPret = 0;

                // Adăugarea datelor în model
                while (resultSet.next()) {
                    String numeServiciu = resultSet.getString("nume_serviciu");
                    int pretServiciu = resultSet.getInt("pret");

                    modelTabel.addRow(new Object[]{numeServiciu, pretServiciu});
                    totalPret += pretServiciu;
                }
                // Adăugarea prețul total la model
                modelTabel.addRow(new Object[]{"", totalPret});

                // Afișarea modelului într-un tabel
                JTable tabelServicii = new JTable(modelTabel);
                JScrollPane scrollPane = new JScrollPane(tabelServicii);
                scrollPane.setPreferredSize(new Dimension(400, 200));

                // Afișarea tabelului într-un dialog
                JOptionPane.showMessageDialog(this, scrollPane, "Servicii medicale", JOptionPane.PLAIN_MESSAGE);
            }

            // Închiderea conexiunii
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la conectarea la baza de date sau interogarea bazei de date.");
        }
    }

    private void insertPacientIntoDatabase(String cnpPacient) {
        try {
            // Configurarea conexiunii la baza de date
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");

            // Crearea instrucțiunii SQL pentru inserarea datelor
            String sql = "INSERT INTO pacient (cnp) VALUES (?)";

            // Prepararea instrucțiunii SQL
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // Setarea valorii parametrilor în instrucțiunea SQL
                preparedStatement.setString(1, cnpPacient);

                // Executarea instrucțiunii SQL
                preparedStatement.executeUpdate();
            }

            // Închiderea conexiunii
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace(); // Tratează excepțiile în mod corespunzător
            JOptionPane.showMessageDialog(null, "Eroare la înregistrarea pacientului.");
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Receptioner();
            }
        });

    }

}
