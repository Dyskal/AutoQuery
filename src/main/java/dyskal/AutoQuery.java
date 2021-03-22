package dyskal;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import java.sql.*;
import java.util.Scanner;

public class AutoQuery {
    private static final TomlManager tomlManager = new TomlManager();
    private static String dbname = tomlManager.getDbname();
    private static String path = tomlManager.getPath();
    private static String username = tomlManager.getUsername();
    private static String password = tomlManager.getPassword();

    public static void main(String[] args) throws SQLException {
        if (args.length == 1 && Boolean.parseBoolean(args[0]) || dbname.equals("placeholder")) {
            inputData();
        }

        PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();
        pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
        pds.setURL("jdbc:oracle:thin:@" + dbname + "_high?TNS_ADMIN=" + path);
        pds.setUser(username);
        pds.setPassword(password);
        pds.setConnectionPoolName("JDBC_UCP_POOL");

        try (Connection conn = pds.getConnection()) {
            queries(conn.createStatement());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Login data must be wrong: re-enter your login creditentials");
            inputData();

            pds = PoolDataSourceFactory.getPoolDataSource();
            pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
            pds.setURL("jdbc:oracle:thin:@" + dbname + "_high?TNS_ADMIN=" + path);
            pds.setUser(username);
            pds.setPassword(password);
            pds.setConnectionPoolName("JDBC_UCP_POOL_0");

            try (Connection conn = pds.getConnection()) {
                queries(conn.createStatement());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void inputData() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Database name:");
        String input = scanner.nextLine();
        if (!input.isEmpty()) { tomlManager.setDbname(input); }
        dbname = tomlManager.getDbname();
        System.out.println("Enter Wallet Path:");
        input = scanner.nextLine();
        if (!input.isEmpty()) { tomlManager.setPath(input); }
        path = tomlManager.getPath();
        System.out.println("Enter Username:");
        input = scanner.nextLine();
        if (!input.isEmpty()) { tomlManager.setUsername(input); }
        username = tomlManager.getUsername();
        System.out.println("Enter Password:");
        input = scanner.nextLine();
        if (!input.isEmpty()) { tomlManager.setPassword(input); }
        password = tomlManager.getPassword();

        for (int i = 0; i < 65; i++) {
            System.out.println();
        }
    }

    private static void queries(Statement statement) throws SQLException {
//            To create non-select queries
//            statement.addBatch("DELETE FROM ...");
//            statement.addBatch("INSERT INTO ...");
//            statement.executeBatch();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM V$VERSION");
        ResultSetMetaData metaData = resultSet.getMetaData();

        System.out.println();
        while (resultSet.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                System.out.println(metaData.getColumnName(i));
                System.out.println("---------------------------------");
                System.out.println(resultSet.getString(i));
                System.out.println();
            }
        }
    }
}
