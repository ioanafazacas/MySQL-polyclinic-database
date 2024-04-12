import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Admin2 extends JFrame {
    private JTabbedPane General;
    private JTextField CNPTextField;
    private JTextField UsernameATF;
    private JTextField ParolaATF;
    private JButton adaugaButton;
    private  JPanel Admin2;
    private JButton StergeBtn;
    private JLabel UsernameLS;
    private JTextField UsernameTFS;
    private JTextField ParolaTFS;
    private JLabel UsernameVLM;
    private JLabel UsernameNouLM;
    private JLabel ParolaNouaLM;
    private JButton ModificaBtn;
    private JTextField UsernameVechiTL;
    private JTextField UsernameNouTL;
    private JTextField ParolaNouaTL;
    private JTextField textField1;
    private JTextField textField4;
    private JTextField textField5;
    private JButton logOutButton;


    public Admin2(String loggedUsername) {
        UserDetails userDetails = getUserDetails(loggedUsername);
        // Display user details in text fields
        textField1.setText(userDetails.getNume());
        textField4.setText(userDetails.getPrenume());
       // textField5.setText(userDetails.getFunctie());
        setTitle("Administrare");
        setContentPane(Admin2);
        setSize(500, 550);
        //setLocationRelativeTo();
        /// CNPTextField.setMaximumSize(100,100);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);


        adaugaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get values from text fields
                String cnp = CNPTextField.getText();
                String username = UsernameATF.getText();
                String password = ParolaATF.getText();
                String role = "angajat"; // Assuming the default role is "angajat"

                // Insert new user into the database
                insertUser(cnp, username, password, role);

                // Clear text fields after insertion
                CNPTextField.setText("");
                UsernameTFS.setText("");
                ParolaTFS.setText("");
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Închide fereastra actuală
                dispose();

                LoginForm2 loginForm2 = new LoginForm2();
                loginForm2.setVisible(true);
            }
        });

        StergeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the username from the text field
                String usernameToDelete = UsernameTFS.getText();

                // Delete user from the database
                deleteUser(usernameToDelete);

                // Clear text field after deletion
                UsernameTFS.setText("");
            }
        });

        ModificaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get values from text fields
                String usernameVechi = UsernameVechiTL.getText();
                String usernameNou = UsernameNouTL.getText();
                String parolaNoua = ParolaNouaTL.getText();

                // Modify user in the database
                modifyUser(usernameVechi, usernameNou, parolaNoua);

                // Clear text fields after modification
                UsernameVechiTL.setText("");
                UsernameNouTL.setText("");
                ParolaNouaTL.setText("");
            }
        });

    }

    // Method to get the id_angajat based on CNP
    private int getAngajatId(String cnp) {
        String url = "jdbc:mysql://localhost/policlinici7";
        String user = "root";
        String pass = "parolaproiect123";
        int angajatId = -1; // Default value if CNP is not found

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            String query = "SELECT id_angajat FROM angajat WHERE CNP = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, cnp);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        angajatId = resultSet.getInt("id_angajat");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return angajatId;
    }

    // Method to insert a new user into the database
    private void insertUser(String cnp, String username, String password, String role) {
        int angajatId = getAngajatId(cnp);

        if (angajatId != -1) {
            String url = "jdbc:mysql://localhost/policlinici7";
            String user = "root";
            String pass = "parolaproiect123";

            try (Connection connection = DriverManager.getConnection(url, user, pass)) {
                String query = "INSERT INTO utilizator (id_angajat, username, parola, tip) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, angajatId);
                    preparedStatement.setString(2, username);
                    preparedStatement.setString(3, password);
                    preparedStatement.setString(4, role);

                    preparedStatement.executeUpdate();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Angajatul nu a fost gasit pentru CNP-ul dat.", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser(String usernameToDelete) {
        String url = "jdbc:mysql://localhost/policlinici7";
        String user = "root";
        String pass = "parolaproiect123";

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            String query = "DELETE FROM utilizator WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, usernameToDelete);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Utilizatorul a fost sters cu succes.", "Succes", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Utilizatorul nu a fost gasit.", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }

    // Method to modify a user in the database based on username
    private void modifyUser(String usernameVechi, String usernameNou, String parolaNoua) {
        String url = "jdbc:mysql://localhost/policlinici7";
        String user = "root";
        String pass = "parolaproiect123";

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            // Check if the user with the old username exists
            if (userExists(usernameVechi)) {
                // Modify username and password
                String query = "UPDATE utilizator SET username = ?, parola = ? WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, usernameNou);
                    preparedStatement.setString(2, parolaNoua);
                    preparedStatement.setString(3, usernameVechi);

                    preparedStatement.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Utilizatorul a fost modificat cu succes.", "Succes", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Utilizatorul cu username-ul vechi nu a fost gasit.", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to check if a user with the specified username exists
    private boolean userExists(String username) {
        String url = "jdbc:mysql://localhost/policlinici7";
        String user = "root";
        String pass = "parolaproiect123";

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            String query = "SELECT COUNT(*) FROM utilizator WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception according to your application's requirements
            return false;
        }
    }

    // Method to retrieve user details by username
    private UserDetails getUserDetails(String username) {
        UserDetails userDetails = new UserDetails();

        String url = "jdbc:mysql://localhost/policlinici7";
        String user = "root";
        String pass = "parolaproiect123";

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            String query = "SELECT nume, prenume, functia FROM persoana,angajat, utilizator WHERE utilizator.username=? and utilizator.id_angajat=angajat.id_angajat and persoana.CNP=angajat.CNP";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        userDetails.setNume(resultSet.getString("nume"));
                        userDetails.setPrenume(resultSet.getString("prenume"));
                        userDetails.setFunctie(resultSet.getString("functia"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return userDetails;
    }




    public static void main(String[] args) {

        new Admin2(null);
    }

    private static class UserDetails {
        private String nume;
        private String prenume;
        private String functie;

        public String getNume() {
            return nume;
        }

        public void setNume(String nume) {
            this.nume = nume;
        }

        public String getPrenume() {
            return prenume;
        }

        public void setPrenume(String prenume) {
            this.prenume = prenume;
        }

        public String getFunctie() {
            return functie;
        }

        public void setFunctie(String functie) {
            this.functie = functie;
        }
    }

}
