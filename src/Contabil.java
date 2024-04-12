import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class Contabil extends JFrame{

    public  String username;
    public  JTextField NumeAng;
    public  JTextField PrenumeAng;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JPanel venitPanel;
    private JPanel profitMedicPanel;
    private JTextField textFieldprenumeC;
    private JTextField textFieldnumeC;
    private JTextField textFieldfunctieC;
    private JTextArea textAreaOrar;
    private JTextArea textAreaConcedii;
    private JTextArea textAreaSalariu;
    private JButton cautaButton;
    private JTextField textFieldMedic;
    private JButton vizualizareSalariiButton;
    private JButton vizualizareOrarButton;
    private JButton vizualizareConcediiButton;
    private JButton logOutButton;
    private JTextField contabilTextField;
    public int id_angajat;


    public Contabil()
    {
        System.out.println("id -------- "+id_angajat+"- ");
        setTitle("Contabil");
        setContentPane(panel1);
        setSize(450, 500);
        //setLocationRelativeTo();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        afisareVenituri();
        System.out.println(id_angajat);

        cautaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obține CNP-ul introdus în textFieldMedic
                String cnpMedic = textFieldMedic.getText();

                // Obține ID-ul angajatului asociat CNP-ului din baza de date
                int idAngajat = getIdAngajatByCNP(cnpMedic);

                // Verifică dacă s-a găsit un angajat cu CNP-ul respectiv
                if (idAngajat != -1) {
                    // Afișează salariile angajatului într-o nouă fereastră
                    afisareSalariiAngajat(idAngajat);
                } else {
                    JOptionPane.showMessageDialog(null, "Angajatul cu CNP-ul specificat nu a fost găsit.");
                }
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

    }


    private int getIdAngajatByCNP(String cnp) {
        int idAngajat = -1;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123")) {
            String query = "SELECT id_angajat FROM angajat WHERE CNP = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, cnp);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        idAngajat = resultSet.getInt("id_angajat");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la conectarea la baza de date sau interogarea angajatului.");
        }

        return idAngajat;
    }

    // Metoda pentru a afișa salariile angajatului într-o fereastră
    private void afisareSalariiAngajat(int idAngajat) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123")) {
            // Apelul procedurii stocate pentru a obține salariile angajatului
            String callProcedure = "{call afisari_salarii_angajat(?)}";
            CallableStatement callableStatement = connection.prepareCall(callProcedure);
            callableStatement.setInt(1, idAngajat);

            ResultSet resultSetSalarii = callableStatement.executeQuery();

            // Afișează salariile într-o nouă fereastră
            JTable tableSalarii = new JTable(buildTableModel(resultSetSalarii));
            JScrollPane scrollPaneSalarii = new JScrollPane(tableSalarii);

            JFrame frameSalarii = new JFrame("Salarii Angajat");
            frameSalarii.getContentPane().add(scrollPaneSalarii, BorderLayout.CENTER);
            frameSalarii.setSize(400, 300);
            frameSalarii.setVisible(true);

            // Închide resursele
            resultSetSalarii.close();
            callableStatement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la conectarea la baza de date sau apelarea procedurii stocate.");
        }
    }

    private void afisareVenituri() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123")) {
            String sql = "CALL afisari_profit_policlinica()";

            try (CallableStatement statement = connection.prepareCall(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                JTable table = new JTable(buildTableModel(resultSet));
                JScrollPane scrollPane = new JScrollPane(table);

                venitPanel.setLayout(new BorderLayout());
                venitPanel.add(scrollPane, BorderLayout.CENTER);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        JFrame concediiFrame = new JFrame("Concedii Contabil");
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
        JFrame orarFrame = new JFrame("Orar Contabil");
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
        JFrame salariiFrame = new JFrame("Salarii Contabil");
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


    // Metoda pentru a construi un model de tabel dintr-un ResultSet
    public static DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        // Numele coloanelor
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Datele tabelului
        Vector<Vector<Object>> data = new Vector<>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(resultSet.getObject(columnIndex));
            }
            data.add(vector);
        }

        // Crearea modelului de tabel
        return new DefaultTableModel(data, columnNames);
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

                new Contabil();
            }
        });

    }
}
