package dataBase;
/*
Спроектировать базу «Квартиры». Каждая запись в базе содержит данные о квартире (район,
адрес, площадь, кол. комнат, цена). Сделать возможность выборки квартир из списка по параметрам.
*/
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/flatsDb";
    static final String DB_USER = "root"; //пользователь
    static final String DB_PASSWORD = "testpass"; // пароль

    static Connection conn;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            try {
                // create connection
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                initDB();

                while (true) {
                    System.out.println("1: add flat");
                    System.out.println("2: add random flats");
                    System.out.println("3: delete flat");
                    System.out.println("4: change flat");
                    System.out.println("5: view flats");
                    System.out.println("6: found flats with parameters");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addFlat(sc);
                            break;
                        case "2":
                            insertRandomFlats(sc);
                            break;
                        case "3":
                            deleteFlat(sc);
                            break;
                        case "4":
                            changeFlat(sc);
                            break;
                        case "5":
                            viewFlats();
                            break;
                        case "6":
                            foundFlats(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void initDB() throws SQLException {
        try( Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS Flats");
            st.execute("CREATE TABLE Flats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                                            "district VARCHAR(30) NOT NULL, " +
                                            "adress VARCHAR(30) NOT NULL UNIQUE KEY, " +
                                            "area INT NOT NULL, " +
                                            "romsCount INT NOT NULL, " +
                                            "price INT NOT NULL" +
                                            ")");
        }
    }

    private static void addFlat(Scanner sc) throws SQLException {
        System.out.print("Enter district: ");
        String district = sc.nextLine();
        System.out.print("Enter adress: ");
        String adress = sc.nextLine();
        System.out.print("Enter area: ");
        String sArea = sc.nextLine();
        int area = Integer.parseInt(sArea);
        System.out.print("Enter romsCount: ");
        String sRomsCount = sc.nextLine();
        int romsCount = Integer.parseInt(sRomsCount);
        System.out.print("Enter price: ");
        String sPrice = sc.nextLine();
        int price = Integer.parseInt(sPrice);

        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO Flats (district, adress, area, romsCount, price)" +
                                                        " VALUES(?, ?, ?, ?, ?)");) {
            ps.setString(1, district);
            ps.setString(2, adress);
            ps.setInt(3, area);
            ps.setInt(4, romsCount);
            ps.setInt(5, price);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        }
    }

    private static void deleteFlat(Scanner sc) throws SQLException {
        System.out.print("Enter flat adress: ");
        String adress = sc.nextLine();

        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM Flats WHERE adress = ?")) {
            ps.setString(1, adress);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        }
    }

    private static void changeFlat(Scanner sc) throws SQLException {
        System.out.print("Enter flat adress for change: ");
        String adress = sc.nextLine();
        if(!adress.isEmpty()) {
            System.out.println("Enter parameters for change only! ");
            System.out.print("Enter district: ");
            String district = sc.nextLine();
            System.out.print("Enter area: ");
            String sArea = sc.nextLine();
            System.out.print("Enter romsCount: ");
            String sRomsCount = sc.nextLine();
            System.out.print("Enter price: ");
            String sPrice = sc.nextLine();
            int area = 0;
            int romsCount = 0;
            int price = 0;
            boolean first = true;

            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE Flats SET ");
            if(!district.isEmpty()){
                sb.append("district = ?");
                first = false;
            }
            if(!sArea.isEmpty()){
                if(!first){
                    sb.append(",");
                }
                area = Integer.parseInt(sArea);
                sb.append("area = ?");
                first = false;
            }
            if(!sRomsCount.isEmpty()){
                if(!first){
                    sb.append(",");
                }
                romsCount = Integer.parseInt(sRomsCount);
                sb.append("romsCount = ?");
                first = false;
            }
            if(!sPrice.isEmpty()){
                if(!first){
                    sb.append(",");
                }
                price = Integer.parseInt(sPrice);
                sb.append("price = ?");
                first = false;
            }
            sb.append(" WHERE adress = ?");

            if(first){
                System.out.println("No parameter for change");
                return;
            }

            try(PreparedStatement ps = conn.prepareStatement(sb.toString())) {
                int counter = 1;
                if(!district.isEmpty()){
                    ps.setString(counter, district);
                    counter++;
                }
                if(!sArea.isEmpty()){
                    ps.setInt(counter, area);
                    counter++;
                }
                if(!sRomsCount.isEmpty()){
                    ps.setInt(counter, romsCount);
                    counter++;
                }
                if(!sPrice.isEmpty()){
                    ps.setInt(counter, price);
                    counter++;
                }
                ps.setString(counter, adress);
                ps.executeUpdate(); // for INSERT, UPDATE & DELETE
            }
        }
        System.out.println("Empty flat for change adress!");
    }

    private static void insertRandomFlats(Scanner sc) throws SQLException {
        System.out.print("Enter flats count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);
        Random rnd = new Random();
        conn.setAutoCommit(false); // enable transactions
        try {
            try {
                try(PreparedStatement ps = conn.prepareStatement("INSERT INTO Flats (district, adress, area, romsCount, price)" +
                                                                    " VALUES(?, ?, ?, ?, ?)")) {
                    for (int i = 0; i < count; i++) {
                        ps.setString(1, "District" + rnd.nextInt(100));
                        ps.setString(2, "Adress" + i);
                        ps.setInt(3, rnd.nextInt(100));
                        ps.setInt(4, rnd.nextInt(10));
                        ps.setInt(5, rnd.nextInt(1000000));
                        ps.executeUpdate();
                    }
                    conn.commit();
                }
            } catch (Exception ex) {
                conn.rollback();
            }
        } finally {
            conn.setAutoCommit(true); // return to default mode
        }
    }

    private static void viewFlats() throws SQLException {
        printSelect("SELECT * FROM Flats");
    }

    private static void foundFlats(Scanner sc) throws SQLException {

        System.out.println("Enter parameters for search only! ");

        System.out.print("Enter district: ");
        String district = sc.nextLine();
        System.out.print("Enter flat adress: ");
        String adress = sc.nextLine();
        System.out.print("Enter area: ");
        String sArea = sc.nextLine();
        System.out.print("Enter romsCount: ");
        String sRomsCount = sc.nextLine();
        System.out.print("Enter price: ");
        String sPrice = sc.nextLine();
        int area = 0;
        int romsCount = 0;
        int price = 0;
        boolean first = true;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Flats WHERE ");
        if (!district.isEmpty()) {
            sb.append("district=");
            sb.append("\"");
            sb.append(district);
            sb.append("\"");
            first = false;
        }
        if (!adress.isEmpty()) {
            if (!first) {
                sb.append(" AND ");
            }
            sb.append("adress=");
            sb.append("\"");
            sb.append(adress);
            sb.append("\"");
            first = false;
        }
        if (!sArea.isEmpty()) {
            area = Integer.parseInt(sArea);
            if (!first) {
                sb.append(" AND ");
            }
            sb.append("area=");
            sb.append("\"");
            sb.append(area);
            sb.append("\"");
            first = false;
        }
        if (!sRomsCount.isEmpty()) {
            romsCount = Integer.parseInt(sRomsCount);
            if (!first) {
                sb.append(" AND ");
            }
            sb.append("romsCount=");
            sb.append("\"");
            sb.append(romsCount);
            sb.append("\"");
            first = false;
        }
        if (!sPrice.isEmpty()) {
            price = Integer.parseInt(sPrice);
            if (!first) {
                sb.append(" AND ");
            }
            sb.append("price=");
            sb.append("\"");
            sb.append(price);
            sb.append("\"");
            first = false;
        }

        if (first) {
            System.out.println("No parameter for search");
            return;
        }
        printSelect(sb.toString());
    }

    private static void printSelect(String select)throws SQLException{
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            // table of data representing a database result set,
            try (ResultSet rs = ps.executeQuery()) {//курсор
                // can be used to get information about the types and properties of the columns in a ResultSet object
                ResultSetMetaData md = rs.getMetaData(); //описание курсора, с оинформацией о таблице

                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.printf("%15s", md.getColumnName(i));
                System.out.println();

                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.printf("%15s", rs.getString(i));
                    }
                    System.out.println();
                }
            }
        }
    }
}
