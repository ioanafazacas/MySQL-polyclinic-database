import java.sql.*;

public class Modul1 {

    public static void afisareOrarInspector(String nume, String prenume, String functia) {
        // Codul pentru execuția procedurii stocate afisare_orar_inspector
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");
             CallableStatement callStatement = connection.prepareCall("{CALL afisare_orar_inspector(?,?,?,?)}")) {
            ResultSetMetaData rsmd = null;
            // Setează valorile pentru parametrii procedurii
            callStatement.setString(1, nume);
            callStatement.setString(2, prenume);
            callStatement.setString(3, functia);
            callStatement.registerOutParameter(4, Types.INTEGER); // Se înregistrează parametrul OUT

            // Execută procedura stocată
            callStatement.execute();
            int ind =callStatement.getInt(4);
            // System.out.println(ind);
            if (ind==1) {
                ResultSet resultSet = callStatement.getResultSet();
                rsmd = resultSet.getMetaData();
                int columnCount = rsmd.getColumnCount();

                // Parcurge rezultatul și afișează informațiile
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.println(rsmd.getColumnName(i) + ": " + resultSet.getString(i));
                    }
                    System.out.println(); // Adaugă un spațiu între înregistrări pentru claritate
                }
            }
            else
            {
                System.out.println("Nu exista un angajat cu detaliile specificate");
            }

            // Restul codului pentru a trata rezultatul
            // ...
        } catch (SQLException sqlex) {
            System.err.println("An SQL Exception occurred. Details are provided below:");
            sqlex.printStackTrace(System.err);
        }
    }

    public static void verificareConcediu(String idAngajat, String data) {
        // Codul pentru execuția procedurii stocate verificare_concediu
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");
             CallableStatement callStatement = connection.prepareCall("{CALL verificare_concediu(?,?,?)}")) {

            // Setează valorile pentru parametrii procedurii
            callStatement.setString(1, data);
            callStatement.setString(2, idAngajat);
            callStatement.registerOutParameter(3, Types.INTEGER); // Se înregistrează parametrul OUT

            // Execută procedura stocată
            callStatement.execute();

            int ind =callStatement.getInt(3);
            // System.out.println(ind);
            if (ind==0) {
                System.out.println("Angajatul este in concediu");
            }
            else
            {
                System.out.println("Angajatul e disponibil");
            }

        } catch (SQLException sqlex) {
            System.err.println("An SQL Exception occurred. Details are provided below:");
            sqlex.printStackTrace(System.err);
        }

    }
    public static void print_concedii(String idAngajat) {
        // Codul pentru execuția procedurii stocate verificare_concediu
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/policlinici7?user=root&password=parolaproiect123");
             CallableStatement callStatement = connection.prepareCall("{CALL print_concedii_angajat(?,?)}")) {

            // Setează valorile pentru parametrii procedurii
            callStatement.setString(1, idAngajat);
            callStatement.registerOutParameter(2, Types.INTEGER); // Se înregistrează parametrul OUT

            // Execută procedura stocată

            callStatement.execute();
            int ind =callStatement.getInt(2);
            // System.out.println(ind);
            if (ind==1) {
                System.out.println("Angajatul nu are concedii");
            }
            else
            {
                ResultSetMetaData rsmd = null;
                ResultSet resultSet = callStatement.getResultSet();
                rsmd = resultSet.getMetaData();
                int columnCount = rsmd.getColumnCount();

                // Parcurge rezultatul și afișează informațiile
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.println(rsmd.getColumnName(i) + ": " + resultSet.getString(i));
                    }
                    System.out.println(); // Adaugă un spațiu între înregistrări pentru claritate
                }
            }

        } catch (SQLException sqlex) {
            System.err.println("An SQL Exception occurred. Details are provided below:");
            sqlex.printStackTrace(System.err);
        }
    }
}