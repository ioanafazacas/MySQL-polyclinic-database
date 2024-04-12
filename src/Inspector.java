import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Inspector extends JFrame{
    private JTabbedPane resursePanel;
    private JLabel functieLabel;
    public  String username;
    public  JTextField NumeAng;
    public  JTextField PrenumeAng;
    public int id_angajat;
    private JLabel prenumeLabel;
    private JTextField numeFieldC;
    private JTextField prenumeFieldC;
    private JTextField functieFieldC;
    private JLabel concediiLabel;
    private JTextArea textAreaConcedii;
    private JTextArea textAreaSalariu;
    private JPanel CautareAngajay;
    private JLabel numeLabel;
    private JTextArea textAreaOrar;
    private JLabel orarLabel;
    private JButton cautaButton;
    private JTextField inspectorTextField;
    private JButton logOutButton;
    private JButton vizualizareSalariiButton;
    private JButton vizualizareOrarButton;
    private JButton vizualizareConcediiButton;
    private JTextField textFieldnume;
    private JTextField textFieldprenume;
    private JTextField textFieldfunctie;

    public Inspector()
    {
        setTitle("Inspector");
        setContentPane(resursePanel);
        setSize(450, 500);
        //setLocationRelativeTo();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);



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

        vizualizareSalariiButton.addActionListener(new  ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afiseazaSalariiMedic();
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Închide fereastra actuală
                dispose();

                // Deschide fereastra LoginForm
                LoginForm2 loginForm2 = new LoginForm2();
                loginForm2.setVisible(true);
            }
        });



        cautaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nume = numeFieldC.getText();
                String prenume = prenumeFieldC.getText();
                String functie = functieFieldC.getText();
                int idAngajat=0 ;

                try {
                    // Conectare la baza de date
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");

                    // Apelul procedurii stocate pentru a obține orarul angajatului
                    String callProcedure = "{call afisare_orar_inspector(?, ?, ?, ?)}";
                    CallableStatement callableStatement = connection.prepareCall(callProcedure);
                    callableStatement.setString(1, nume);
                    callableStatement.setString(2, prenume);
                    callableStatement.setString(3, functie);
                    callableStatement.registerOutParameter(4, Types.INTEGER);

                    callableStatement.execute();

                    // Verificați rezultatul returnat de procedura stocată
                    int result = callableStatement.getInt(4);

                    if (result == 1) {
                        // Afișați rezultatele în textAreaOrar
                        ResultSet resultSet = callableStatement.getResultSet();
                        StringBuilder orarText = new StringBuilder("Orarul angajatului:\n");
                        while (resultSet.next()) {
                            //String ziSaptamana = resultSet.getString("zi_saptamana");
                            int unitateMed = resultSet.getInt("id_unitate");
                            String timpStart = resultSet.getString("timp_start");
                            String timpFinish = resultSet.getString("timp_finish");

                            String denumireUnitate = getDenumireUnitateMedicala(unitateMed);
                            orarText.append("Unitate: ").append(denumireUnitate).append(" : ");

                            orarText.append(timpStart).append(" - ").append(timpFinish).append("\n");
                        }
                        textAreaOrar.setText(orarText.toString());

                        resultSet.close();
                    } else {
                        textAreaOrar.setText("Angajatul nu a fost găsit sau nu are orar definit.");
                    }

                    // Închidere resurse
                    callableStatement.close();
                    connection.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Eroare la conectarea la baza de date sau apelarea procedurii stocate.");
                }
                try {
                    // Conectare la baza de date
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");
                    String query = "SELECT id_angajat FROM angajat " +
                            "INNER JOIN persoana ON persoana.CNP = angajat.CNP " +
                            "WHERE persoana.nume = ? AND persoana.prenume = ? AND angajat.functia = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, nume);
                    preparedStatement.setString(2, prenume);
                    preparedStatement.setString(3, functie);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        idAngajat = resultSet.getInt("id_angajat");
                        if(idAngajat==0)
                        {
                            System.out.println("nu merge interogarea");
                        }
                        else System.out.println("id angajat " + idAngajat);

                    }

                    // Apelul procedurii stocate pentru a obține concediile angajatului
                    String callProcedure = "{call print_concedii_angajat(?, ?)}";
                    CallableStatement callableStatement = connection.prepareCall(callProcedure);
                    callableStatement.setInt(1, idAngajat);
                    callableStatement.registerOutParameter(2, Types.INTEGER);

                    callableStatement.execute();

                    // Verificați rezultatul returnat de procedura stocată
                    int result = callableStatement.getInt(2);

                    if (result == 0) {
                        // Afișați rezultatele în textAreaConcedii
                        resultSet = callableStatement.getResultSet();
                        StringBuilder concediiText = new StringBuilder("Concediile angajatului:\n");
                        while (resultSet.next()) {
                            Date dataIncepere = resultSet.getDate("data_incepere");
                            Date dataFinalizare = resultSet.getDate("data_finalizare");

                            concediiText.append("Data începerii: ").append(dataIncepere).append("   Data finalizării: ").append(dataFinalizare).append("\n");
                        }
                        textAreaConcedii.setText(concediiText.toString());

                        resultSet.close();
                    } else {
                        textAreaConcedii.setText("Angajatul nu are concedii sau nu a fost găsit.");
                    }

                    // Închidere resurse
                    callableStatement.close();
                    connection.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Eroare la conectarea la baza de date sau apelarea procedurii stocate.");
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
        JFrame concediiFrame = new JFrame("Concedii Inspector");
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
        JFrame orarFrame = new JFrame("Orar Inspector");
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
        JFrame salariiFrame = new JFrame("Salarii Inspector");
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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Inspector();
            }
        });

    }

}

