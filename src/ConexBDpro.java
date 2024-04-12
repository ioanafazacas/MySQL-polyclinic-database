
import java.sql.*;
import java.util.Scanner;

public class ConexBDpro {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        }
        catch (Exception ex) {
            System.err.println("An Exception occured during JDBC Driver loading." +
                    " Details are provided below:");
            ex.printStackTrace(System.err);
        }
        Connection connection = null;
        Statement selectStatement = null, insertStatement = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;


        try {
            connection = DriverManager.
                    getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");
            insertStatement = connection.createStatement();

          //    insertStatement.execute("INSERT INTO utilizator (id_angajat, username, parola, tip) " +
          //          "VALUES ('3','angajat3java','parolajava','administrator')");
          //  insertStatement.execute("INSERT INTO Contacts (FirstName, LastName, HomePhone, MobilePhone, EmailAddress) " +
            //        "VALUES ('Jane','Doe','0040264123456','0040744123456','jane.doe@example.com')");
            selectStatement = connection.createStatement();
            String query = "{CALL afisare_orar_inspector(?,?,?)}";
            CallableStatement stmt = connection.prepareCall(query);
            selectStatement.execute("SELECT * FROM angajat");
            rs = selectStatement.getResultSet();
            rsmd = rs.getMetaData();
            System.out.println("There are " + rsmd.getColumnCount() + " columns in the result set:");
            for (int i = 1; i <= rsmd.getColumnCount(); i++)
                System.out.println("\t Column " + (i) + " is " + rsmd.getColumnName(i));
            int rowCount = 0;
            while(rs.next()){
                System.out.println("Displaying information on row: " + (++rowCount));
                System.out.println("\tid_angajat: " + rs.getString("id_angajat"));
                System.out.println("\tCNP: " + rs.getString("CNP"));
                System.out.println("\tnr_contract: " + rs.getString("nr_contract"));
                System.out.println("\tdata_angajari: " + rs.getString("data_angajari"));
                System.out.println("\tdepartament: " + rs.getString("departament"));
                System.out.println("\tfunctia: " + rs.getString("functia"));
                System.out.println("\tsalariu: " + rs.getString("salariu"));
                System.out.println("\tnr_ore: " + rs.getString("nr_ore"));
            }
        }
        catch(SQLException sqlex) {
            System.err.println("An SQL Exception occured. Details are provided below:");
            sqlex.printStackTrace(System.err);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch(SQLException e) {
                }
                rs = null;
            }
            if (selectStatement != null) {
                try {
                    selectStatement.close();
                }
                catch(SQLException e) {}
                selectStatement = null;
            }
            if (insertStatement != null) {
                try {
                    insertStatement.close();
                }
                catch(SQLException e) {}
                insertStatement = null;
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch(SQLException e) {}
                connection = null;
            }
        }

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
                } catch (Exception ex) {
                    System.err.println("An Exception occurred during JDBC Driver loading. Details are provided below:");
                    ex.printStackTrace(System.err);
                }

                CallableStatement callStatement = null;

        System.out.println("Printare concedii:");
        System.out.println("Introduceti id_angajat: ");
        String idAngajat =  scanner.nextLine();
        Modul1.print_concedii(idAngajat);


        // Pentru afișarea orarului inspectorului
        System.out.println("Introduceți un nume:");
        String nume = scanner.nextLine();

        System.out.println("Introduceți un prenume:");
        String prenume = scanner.nextLine();

        System.out.println("Introduceți o funcție:");
        String functia = scanner.nextLine();

        Modul1.afisareOrarInspector(nume, prenume, functia);

        // Pentru verificarea concediului
        System.out.println("Introduceți id_angajat:");
         idAngajat = scanner.nextLine();

        System.out.println("Introduceți data:");
        String data = scanner.nextLine();

        Modul1.verificareConcediu(idAngajat, data);

        scanner.close();
        }

        }

