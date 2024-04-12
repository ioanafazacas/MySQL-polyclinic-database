import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Scanner;

import static java.sql.DriverManager.getConnection;


public class LoginForm2 extends JFrame  {
    private JPanel panel1;
    private JButton loginButton;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private boolean isMedic;
    private boolean isReceptioner;
    private boolean isContabil;
    private boolean isInspector;
    private boolean isAsistent;
    public String numeAngajat;
    public int id;
    public String prenumeAngajat;


    public LoginForm2() {
        // Configurare frame
        setContentPane(panel1);
        setSize(550, 500);
        //setLocationRelativeTo();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);

                // Verificare utilizator și parolă (implementare personalizată)
                if (isValidLogin(username, password) == false) {
                    JOptionPane.showMessageDialog(null, "Logare eșuată. Verificați utilizatorul și parola.");
                }
            }
        });
    }


    public String getUsername() {
        return this.usernameField.getText();
    }



    // Metoda de verificare a datelor de logare (implementare personalizată)



    private void openNewWindow(String userType) {
        // JFrame newFrame = new JFrame("Fereastra Noua");

        // Stabiliți dimensiunile ferestrei în funcție de tipul utilizatorului
        if ("angajat".equals(userType)) {
            this.dispose();
            String username = usernameField.getText();
            if (isMedic) {
                Medic2 medic2 = new Medic2();

                medic2.NumeAng.setText(numeAngajat);  // Completează numele medicului
                medic2.PrenumeAng.setText(prenumeAngajat);
                medic2.username=usernameField.getText();

                medic2.id_angajat=id;

            }
            if(isAsistent){
                System.out.println("asistentok");
                AsistentMedical asistent = new AsistentMedical();
                asistent.NumeAng.setText(numeAngajat);  // Completează numele asistentului
                asistent.PrenumeAng.setText(prenumeAngajat);
                asistent.username=usernameField.getText();
                asistent.id_angajat=id;
            }
            if (isReceptioner) {
                Receptioner receptioner = new Receptioner();
                receptioner.NumeAng.setText(numeAngajat);  // Completează numele medicului
                receptioner.PrenumeAng.setText(prenumeAngajat);
                receptioner.username=usernameField.getText();
                receptioner.id_angajat=id;

            }
            if (isInspector) {
                Inspector inspector = new Inspector();
                inspector.NumeAng.setText(numeAngajat);  // Completează numele medicului
                inspector.PrenumeAng.setText(prenumeAngajat);
                inspector.username=usernameField.getText();
                inspector.id_angajat=id;

            }
            if (isContabil) {
                Contabil contabil = new Contabil();
                contabil.NumeAng.setText(numeAngajat);  // Completează numele medicului
                contabil.PrenumeAng.setText(prenumeAngajat);
                contabil.username=usernameField.getText();
                System.out.println("id - "+id+"- ");
                contabil.id_angajat=id;


            }
        }else if ("administrator".equals(userType)) {
            this.dispose();
            String username = usernameField.getText();

            new Admin2(username);
        } else if ("super-administrator".equals(userType)) {
            this.dispose();
            String username = usernameField.getText();

            new SuperAdmin(username);
        } else {
            // Tip de utilizator necunoscut
            JOptionPane.showMessageDialog(null, "Tip de utilizator necunoscut.");
            return;
        }


    }

    public int getID()
    {
        return id;
    }
    private boolean isValidLogin(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Conectare la baza de date
            connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");

            // Interogare pentru a verifica dacă există un utilizator cu datele furnizate
            String query = "SELECT a.*, u.tip, p.nume, p.prenume FROM angajat a " +
                    "INNER JOIN utilizator u ON a.id_angajat = u.id_angajat " +
                    "INNER JOIN persoana p ON a.CNP = p.CNP " +
                    "WHERE u.username=? AND u.parola=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            // Verificare dacă s-a găsit un rând în rezultate
            if (resultSet.next()) {
                // Obțineți tipul utilizatorului
                String userType = resultSet.getString("tip");

                numeAngajat = resultSet.getString("nume");
                prenumeAngajat = resultSet.getString("prenume");
                id=resultSet.getInt("id_angajat");
                String angajat_aux=resultSet.getString("functia");
                isMedic = "medic".equals(angajat_aux);
                isAsistent="asistent medical".equals(angajat_aux);
                isContabil="expert financiar contabil".equals(angajat_aux);

                isReceptioner="receptioner".equals(angajat_aux);
                isInspector="inspector resurse umane".equals(angajat_aux);
                System.out.println("Angajat aux: "+angajat_aux);
                // Verificați dacă utilizatorul este de tip "angajat", "administrator" sau "super-administrator"
                if ("angajat".equals(userType) || "administrator".equals(userType) || "super-administrator".equals(userType)) {
                    openNewWindow(userType);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Acces interzis pentru acest tip de utilizator.");
                    return false;
                }
            } else {
                //JOptionPane.showMessageDialog(null, "Logare eșuată. Verificați utilizatorul și parola.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Închidere resurse (ResultSet, PreparedStatement, Connection)
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Cauta Look and Feel-ul Nimbus
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                new LoginForm2();
            }
        });
    }
}
