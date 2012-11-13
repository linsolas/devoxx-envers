package devoxx.envers.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.h2.tools.Server;

/**
 * User: Romain Linsolas Date: 06/11/12
 */
public class DBUtils {

    private static final String DATABASE_URL = "jdbc:h2:mem:envers;DB_CLOSE_DELAY=-1";

    private static final int CELL_WIDTH = 16;

    private static Server h2Server;

    private static DataSource dataSource;

    private static EntityManager entityManager;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");

    public static void initDatabase() throws SQLException {
        initHibernate();
        h2Server = Server.createTcpServer("-tcpAllowOthers");
        h2Server.start();
        dataSource = new JdbcDataSource();
        ((JdbcDataSource) dataSource).setURL(DATABASE_URL);
        runScript(DBUtils.class.getResourceAsStream("/h2-scripts.sql"));
    }

    private static void initHibernate() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("enversPU");
        entityManager = emf.createEntityManager();
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static void runScript(InputStream input) throws SQLException {
        RunScript.execute(getConnection(), new InputStreamReader(input));
    }

    public static void displayDBContent() throws SQLException {
        displayTableContent("T_PERSON");
        displayTableContent("T_PERSON_AUD");
        displayTableContent("REVINFO");
    }

    public static void displayTableContent(String tableName) throws SQLException {
        PreparedStatement ps = null;
        ResultSet set = null;
        int count = 0;
        StringBuffer content = new StringBuffer();
        try {
            ps = getConnection().prepareStatement("select * from " + tableName);
            set = ps.executeQuery();
            ResultSetMetaData meta = set.getMetaData();
            content.append(displayHeader(tableName, meta));
            while (set.next()) {
                int col = 1;
                content.append("\t|").append(StringUtils.center(String.valueOf(++count), 4));
                while (col <= meta.getColumnCount()) {
                    String val = StringUtils.trimToEmpty(set.getString(col));
                    if ("REVTSTMP".equals(meta.getColumnName(col))) {
                        val = sdf.format(new Date(Long.parseLong(val)));
                    }
                    col++;
                    String str = StringUtils.center(StringUtils.abbreviate(val, CELL_WIDTH), CELL_WIDTH);
                    content.append("|").append(str);
                }
                content.append("|\n").append(addLine(meta));
            }
        } finally {
            closeQuietly(set);
            closeQuietly(ps);
        }
        System.out.print(content);
    }

    private static String displayHeader(String tableName, ResultSetMetaData meta) throws SQLException {
        StringBuffer sb = new StringBuffer();
        sb.append("\t  +").append(StringUtils.repeat("-", tableName.length() + 2)).append("+\n\t  | ").append(tableName).append(" |\n");
        int nbCols = meta.getColumnCount();
        sb.append(addLine(meta));
        sb.append("\t|    |");
        for (int i = 1; i <= nbCols; i++) {
            String n = meta.getColumnName(i);
            n = StringUtils.center(StringUtils.abbreviate(n, CELL_WIDTH), CELL_WIDTH);
            sb.append(n).append('|');
        }
        sb.append('\n').append(addLine(meta));
        return sb.toString();
    }

    private static String addLine(ResultSetMetaData meta) throws SQLException {
        int nbCols = meta.getColumnCount();
        StringBuffer sb = new StringBuffer();
        sb.append("\t+----+");
        for (int i = 0; i < nbCols; i++) {
            sb.append(StringUtils.repeat("-", CELL_WIDTH)).append('+');
        }
        sb.append("\n");
        return sb.toString();
    }

    private static void closeQuietly(Statement stat) throws SQLException {
        if (stat != null) {
            stat.close();
        }
    }

    private static void closeQuietly(ResultSet set) throws SQLException {
        if (set != null) {
            set.close();
        }
    }

}
