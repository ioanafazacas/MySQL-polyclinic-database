import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class AsistentMedical {
    public  String username;
    public  int id_angajat;
    private String idRaport;
    private Boolean parafat;
    private JTabbedPane General;
    public  JTextField NumeAng;
    public  JTextField PrenumeAng;
    private JTextField asistentMedicalTextField;
    private JLabel UsernameLS;
    private JButton CautaPacientIstoric;
    private JTextField CNPCautareIstoricTF;
    private JTextField CNPCautareRaport;
    private JTextField DataCautareRaport;
    private JButton AdaugaRaport;
    private JButton CautaRaport;
    private JLabel UsernameVLM;
    private JButton vizualizareSalariiButton;
    private JButton vizualizareOrarButton;
    private JButton vizualizareConcediiButton;
    private JButton logOutButton;


    public AsistentMedical() {
       // initComponents(); // Metoda pentru a inițializa componentele din form

        JFrame frame = new JFrame("Asistent Medical");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(General); // Sau oricare alt container ar trebui folosit

        frame.pack();
        frame.setVisible(true);

        vizualizareOrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afiseazaOrarAsistent();
            }
        });

        vizualizareConcediiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afiseazaConcediiMedic();
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Închide fereastra actuală
                frame.dispose();

                // Deschide fereastra LoginForm
                LoginForm2 loginForm2 = new LoginForm2();
                loginForm2.setVisible(true);
            }
        });

        vizualizareSalariiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afiseazaSalariiAsistent();
            }
        });

        AdaugaRaport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afiseazaFereastraAdaugaRaport();
            }
        });

        CautaRaport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cnpCautareRaport = CNPCautareRaport.getText();
                String dataCautareRaport = DataCautareRaport.getText();

                // Verificare dacă CNP și data sunt furnizate
                if (cnpCautareRaport.isEmpty() || dataCautareRaport.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Introduceți CNP-ul pacientului și data programării pentru a căuta raportul.");
                    return;
                }

                // Apelarea interogării pentru a obține informațiile despre raport
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123")) {
                    JFrame raportFrame = new JFrame("Raport Medical");
                    raportFrame.setSize(400, 300);
                    JTextArea textArea = new JTextArea();
                    //textArea.setSize(400,300);

                    JButton ParafareRaport = new JButton("Parafare Raport");


                    ParafareRaport.setBounds(95, 200, 200, 30);
                    raportFrame.add(ParafareRaport);



                    // Crearea unui panou pentru a organiza componentele
                    JPanel raportPanel = new JPanel();
                    raportFrame.add(raportPanel);

                    String query="SELECT nume, prenume from persoana pers where pers.cnp=? ";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setString(1, cnpCautareRaport);
                        // pstmt.setString(2, dataCautareRaport);

                        ResultSet resultSet = pstmt.executeQuery();

                        // Afișarea rezultatelor într-un JTextArea

                        while (resultSet.next()) {
                            // Adăugarea informațiilor în JTextArea
                            textArea.append("Nume Pacient: " + resultSet.getString("nume") + "\n");
                            textArea.append("Prenume Pacient: " + resultSet.getString("prenume") + "\n");

                            textArea.append("\n");
                        }


                    }

                    query="SELECT parafa,id,diagnostic, simptome,recomandari  from raport_medical rm,pacient pac where pac.cnp=? and rm.data_consult=? and pac.id_pacient=rm.id_pacient";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setString(1, cnpCautareRaport);
                        pstmt.setString(2, dataCautareRaport);

                        ResultSet resultSet = pstmt.executeQuery();

                        // Afișarea rezultatelor într-un JTextArea

                        while (resultSet.next()) {

                            textArea.append("Data Consultației: " + dataCautareRaport + "\n");
                            textArea.append("Simptome: " + resultSet.getString("simptome") + "\n");
                            textArea.append("Diagnostic: " + resultSet.getString("diagnostic") + "\n");
                            textArea.append("Recomandări: " + resultSet.getString("recomandari") + "\n");
                            idRaport = resultSet.getString("id");
                            parafat="parafat".equals(resultSet.getString("parafa"));
                            if(parafat)
                            {
                                ParafareRaport.hide();
                            }
                            // Adăugarea unui spațiu între rapoarte
                            textArea.append("\n");
                        }

                        // Adăugarea JTextArea la panou
                    }

                    ParafareRaport.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String codParafaMedic = "parafat";  // Setați aici codul de parafă al medicului conectat


                            String cnpPacient = CNPCautareRaport.getText();
                            String dataCautareRaport = DataCautareRaport.getText();

                            String updateQuery = "UPDATE raport_medical SET parafa = ? WHERE id = ?";
                            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123");
                                 PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                                pstmt.setString(1, codParafaMedic);
                                pstmt.setString(2,idRaport);

                                int rowsAffected = pstmt.executeUpdate();

                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(null, "Raportul a fost parafat cu succes!");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Eroare la parafarea raportului. Verificați datele introduse.");
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Eroare la parafarea raportului: " + ex.getMessage());
                            }
                            ParafareRaport.hide();
                        }
                    });

                    query="Select nume_serviciu from servicii s,investigatii inv,raport_medical rm,pacient pac where pac.cnp=? and rm.id_pacient=pac.id_pacient and rm.data_consult=? and inv.id_raport=rm.id and inv.id_servicii=s.id";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setString(1, cnpCautareRaport);
                        pstmt.setString(2, dataCautareRaport);

                        ResultSet resultSet = pstmt.executeQuery();

                        // Afișarea rezultatelor într-un JTextArea
                        textArea.append("Proceduri: \n");

                        while (resultSet.next()) {

                            textArea.append( resultSet.getString("nume_serviciu") + "\n");
                            textArea.append("\n");
                        }

                        // Adăugarea JTextArea la panou
                        raportPanel.add(textArea);

                        // Afișarea ferestrei
                        raportFrame.setVisible(true);}

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Eroare la căutarea raportului: " + ex.getMessage());
                }
            }



        });

        CautaPacientIstoric.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cnpCautare = CNPCautareIstoricTF.getText();

                // Apelarea procedurii stocate pentru a obține rezultatele
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123")) {
                    String callProcedure = "CALL print_raport_pacienti(?)";
                    try (CallableStatement cstmt = connection.prepareCall(callProcedure)) {
                        cstmt.setString(1, cnpCautare);
                        ResultSet resultSet = cstmt.executeQuery();

                        // Crearea și afișarea unei noi ferestre pentru afișarea rezultatelor
                        JFrame raportFrame = new JFrame("Raport Pacienti");
                        raportFrame.setSize(600, 400);

                        // Crearea unui model de tabel pentru afișarea rezultatelor într-un JTable
                        DefaultTableModel model = new DefaultTableModel();
                        JTable table = new JTable(model);
                        raportFrame.add(new JScrollPane(table));

                        // Adăugarea coloanelor la modelul tabelului
                        model.addColumn("Nume");
                        model.addColumn("Prenume");
                        model.addColumn("Diagnostic");
                        model.addColumn("Recomandări");
                        model.addColumn("Nume Serviciu");

                        // Adăugarea rândurilor la modelul tabelului bazat pe rezultatele procedurii stocate
                        while (resultSet.next()) {
                            Object[] row = new Object[]{
                                    resultSet.getString("nume"),
                                    resultSet.getString("prenume"),
                                    resultSet.getString("diagnostic"),
                                    resultSet.getString("recomandari"),
                                    resultSet.getString("nume_serviciu")
                            };
                            model.addRow(row);
                        }

                        // Afișarea ferestrei
                        raportFrame.setVisible(true);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }


    private void initComponents() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(this::createAndShowGUI);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            e.printStackTrace();
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
        JFrame concediiFrame = new JFrame("Concedii Asistent");
        concediiFrame.setSize(600, 400);

        // Creare tabel cu modelul specificat
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Adăugare tabel în fereastră
        concediiFrame.add(scrollPane);

        // Afișare fereastră
        concediiFrame.setVisible(true);
    }


    private void verificaSiCompleteazaNumePrenume() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123");

            // Verificare tip angajat și obținerea numelui și prenumelui dacă este medic
            String query = "SELECT p.nume, p.prenume FROM angajat a " +
                    "INNER JOIN utilizator u ON a.id_angajat = u.id_angajat " +
                    "INNER JOIN persoana p ON a.CNP = p.CNP " +
                    "WHERE u.username = ? AND u.tip = 'asistent'";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "numele_utilizatorului_curent");  // Schimbați cu numele utilizatorului curent

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                NumeAng.setText(resultSet.getString("nume"));
                PrenumeAng.setText(resultSet.getString("prenume"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Asistent medical");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(General);

        frame.pack();
        frame.setVisible(true);
    }

    private void afiseazaOrarAsistent() {
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

    private void afisareOrarInTabel(DefaultTableModel model) {
        JFrame orarFrame = new JFrame("Orar Asistent Medical");
        orarFrame.setSize(600, 400);

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        orarFrame.add(scrollPane);

        orarFrame.setVisible(true);
    }

    private void afiseazaFereastraAdaugaRaport() {
        JFrame adaugaRaportFrame = new JFrame("Adaugare Raport Medical");
        adaugaRaportFrame.setSize(400, 500);

        JPanel adaugaRaportPanel = new JPanel();
        adaugaRaportFrame.add(adaugaRaportPanel);

        adaugaRaportPanel.setLayout(new GridLayout(0, 2));

        JLabel labelNumePacient = new JLabel("Nume Pacient:");
        JTextField textFieldNumePacient = new JTextField(20);

        JLabel labelPrenumePacient = new JLabel("Prenume Pacient:");
        JTextField textFieldPrenumePacient = new JTextField(20);

        JLabel labelNumeMedic = new JLabel("Nume Medic:");
        JTextField textFieldNumeMedic = new JTextField(20);

        JLabel labelPrenumeMedic = new JLabel("Prenume Medic:");
        JTextField textFieldPrenumeMedic = new JTextField(20);

        JLabel labelNumeAsistent = new JLabel("Nume Asistent:");
        JTextField textFieldNumeAsistent = new JTextField(20);

        JLabel labelPrenumeAsistent = new JLabel("Prenume Asistent:");
        JTextField textFieldPrenumeAsistent = new JTextField(20);

        JLabel labelDataConsultatiei = new JLabel("Data Consultatiei:");
        JTextField textFieldDataConsultatiei = new JTextField(20);

        JLabel labelSimptome = new JLabel("Simptome:");
        JTextField textFieldSimptome = new JTextField(20);

        JLabel labelDiagnostic = new JLabel("Diagnostic:");
        JTextField textFieldDiagnostic = new JTextField(20);

        JLabel labelRecomandari = new JLabel("Recomandari:");
        JTextField textFieldRecomandari = new JTextField(20);

        JButton finalizareButton = new JButton("Finalizare");

        finalizareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obțineți valorile introduse în câmpurile text
                String numePacient = textFieldNumePacient.getText();
                String prenumePacient = textFieldPrenumePacient.getText();
                String numeMedic = textFieldNumeMedic.getText();
                String prenumeMedic = textFieldPrenumeMedic.getText();
                String numeAsistent = textFieldNumeAsistent.getText();
                String prenumeAsistent = textFieldPrenumeAsistent.getText();
                String dataConsultatiei = textFieldDataConsultatiei.getText();
                String simptome = textFieldSimptome.getText();
                String diagnostic = textFieldDiagnostic.getText();
                String recomandari = textFieldRecomandari.getText();

                // Efectuați inserarea în tabela "raport_medical"
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123");

                    String query = "INSERT INTO raport_medical (id_pacient, id_medic, id_asistent, data_consult, simptome, diagnostic, recomandari,durata_consultatie) VALUES (?, ?, ?, ?, ?, ?, ?,?)";

                    // Obțineți ID-urile corespunzătoare pentru pacient, medic și asistent (poate fi o altă interogare)
                    int idPacient = getIdPacient(numePacient,prenumePacient);
                    int idMedic = getIdMedic(numeMedic,prenumeMedic);
                    int idAsistent = getIdAsistent(numeAsistent,prenumeAsistent);

                    // Creați declarația pregătită pentru a evita SQL injection
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(1, idPacient);
                        preparedStatement.setInt(2, idMedic);
                        preparedStatement.setInt(3, idAsistent);
                        preparedStatement.setString(4, dataConsultatiei);
                        preparedStatement.setString(5, simptome);
                        preparedStatement.setString(6, diagnostic);
                        preparedStatement.setString(7, recomandari);

                        // Executa operația de inserare
                        preparedStatement.executeUpdate();
                    }


                    adaugaRaportFrame.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();                }
            }});

        adaugaRaportPanel.add(labelNumePacient);
        adaugaRaportPanel.add(textFieldNumePacient);
        adaugaRaportPanel.add(labelPrenumePacient);
        adaugaRaportPanel.add(textFieldPrenumePacient);
        adaugaRaportPanel.add(labelNumeMedic);
        adaugaRaportPanel.add(textFieldNumeMedic);
        adaugaRaportPanel.add(labelPrenumeMedic);
        adaugaRaportPanel.add(textFieldPrenumeMedic);
        adaugaRaportPanel.add(labelNumeAsistent);
        adaugaRaportPanel.add(textFieldNumeAsistent);
        adaugaRaportPanel.add(labelPrenumeAsistent);
        adaugaRaportPanel.add(textFieldPrenumeAsistent);
        adaugaRaportPanel.add(labelDataConsultatiei);
        adaugaRaportPanel.add(textFieldDataConsultatiei);
        adaugaRaportPanel.add(labelSimptome);
        adaugaRaportPanel.add(textFieldSimptome);
        adaugaRaportPanel.add(labelDiagnostic);
        adaugaRaportPanel.add(textFieldDiagnostic);
        adaugaRaportPanel.add(labelRecomandari);
        adaugaRaportPanel.add(textFieldRecomandari);
        adaugaRaportPanel.add(finalizareButton);

        adaugaRaportFrame.setVisible(true);
    }

    private int getIdPacient(String numePacient, String prenumePacient) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql:localhost/policlinici7", "root", "parolaproiect123")) {
            String query = "SELECT id_pacient FROM pacient p, persoana pers WHERE p.cnp=pers.cnp and pers.nume = ? AND pers.prenume = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, numePacient);
                preparedStatement.setString(2, prenumePacient);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id_pacient");
                }
            }
        }
        throw new SQLException("ID-ul pacientului nu a putut fi găsit.");
    }

    private int getIdMedic(String numeMedic, String prenumeMedic) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123")) {
            String query = "Select id_angajat from  persoana p, angajat a where p.nume=? and p.prenume=? and a.cnp=p.cnp";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, numeMedic);
                preparedStatement.setString(2, prenumeMedic);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id_angajat");
                }
            }
        }
        throw new SQLException("ID-ul medicului nu a putut fi găsit.");
    }

    private int getIdAsistent(String numeAsistent, String prenumeAsistent) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7", "root", "parolaproiect123")) {
            String query = "Select id_angajat from  persoana p, angajat a where p.nume=? and p.prenume=? and a.cnp=p.cnp";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, numeAsistent);
                preparedStatement.setString(2, prenumeAsistent);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id_angajat");
                }
            }
        }
        throw new SQLException("ID-ul asistentului nu a putut fi găsit.");
    }

    private void afiseazaSalariiAsistent() {
        // Creare fereastră pentru afișarea salariilor
        JFrame salariiFrame = new JFrame("Salarii Medic");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AsistentMedical());
    }
}