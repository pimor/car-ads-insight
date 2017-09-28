import com.holdenkarau.spark.testing.SharedJavaSparkContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CarsMapUtils;
import util.JsonCarsToCsv;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 25/09/2017.
 */
public class CarsToCsvTest extends SharedJavaSparkContext implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarsToCsvTest.class);
//    private static final String TARGET_DIR = "target/";
    private static final String CSV_OUTPUT_DIR = "cars-csv-data-";
    private static final String FILE_NAME = "cars.sample.json";
    private static final String TABLE_NAME = "cars";
    private static final String H2_CONNECTION = "jdbc:h2:mem:test";
    private static File outputCsvDir = null;

    private Connection conn = null;
    private Statement stmt = null;


    // delete query
    private static final String QUERY_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // Group by uniqueId [tvt2]
    private static final String QUERY_0 =
            "SELECT uniqueId,count(1) num_rows " +
                    "FROM " + TABLE_NAME + " " +
                    "GROUP BY uniqueId " +
                    "HAVING count(1)>1";

    // Group by all fields [tvt3]
    private static final String QUERY_1 =
            "SELECT country,uniqueId,urlAnonymized,make,model,year,mileage,price,doors,fuel,carType," +
                   "transmission,color,region,city,date,titleChunk,contentChunk,count(1) num_rows " +
            "FROM " + TABLE_NAME + " " +
            "GROUP BY country,uniqueId,urlAnonymized,make,model,year,mileage,price,doors,fuel,carType,transmission," +
                     "color,region,city,date,titleChunk,contentChunk " +
            "HAVING count(1)>1";

    // remove urlAnonymized and date [tvt4]
    private static final String QUERY_2 =
            "SELECT country,uniqueId,make,model,year,mileage,price,doors,fuel,carType,transmission,color,region,city," +
                   "count(1) num_rows " +
            "FROM " + TABLE_NAME + " " +
            "GROUP BY country,uniqueId,make,model,year,mileage,price,doors,fuel,carType,transmission,color,region,city " +
            "HAVING COUNT(1)>1";

    // remove price [tvt5]
    private  static final String QUERY_3 =
            "SELECT country,uniqueId,make,model,year,mileage,doors,fuel,carType,transmission,color,region,city," +
                   "count(1) num_rows " +
            "FROM " + TABLE_NAME + " " +
            "GROUP BY country,uniqueId,make,model,year,mileage,doors,fuel,carType,transmission,color,region,city " +
            "HAVING COUNT(1)>1";

    // remove transmission [tvt6]
    private static final String QUERY_4 =
            "SELECT country,uniqueId,make,model,year,mileage,doors,fuel,carType,color,region,city," +
                   "count(1) num_rows " +
            "FROM " + TABLE_NAME + " " +
            "GROUP BY country,uniqueId,make,model,year,mileage,doors,fuel,carType,color,region,city " +
            "HAVING COUNT(1)>1";


    /**
     * Transform the input JSON cars sample file into a CSV file and load it into the H2 database
     * @throws Exception
     */
    @Before
    public void tearUp() throws Exception {
        outputCsvDir = File.createTempFile(CSV_OUTPUT_DIR, "");
//        Files.createTempDirectory("car-ads-csv-output", "");
        outputCsvDir.delete();


        JsonCarsToCsv carsToCsv = new JsonCarsToCsv();

        // read the sample cars json file
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource(FILE_NAME).getFile());

        // transform
        carsToCsv.transformJsonIntoCsv(jsc(), file.getCanonicalPath(), outputCsvDir);

        // load
        carsToCsv.loadCsvIntoH2(outputCsvDir, H2_CONNECTION , "", "", TABLE_NAME, JsonCarsToCsv.CAR_FIELDS_SCHEMA);
    }


    /**
     * Clean the database for the next test execution, just in case other data sample is used
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        executeUpdateQuery(QUERY_DROP_TABLE);
    }


    /**
     * Find duplicate ads based on customer queries and then the results are exported into a CSV file
     * @throws Exception
     */
    @Test
    public void findCarDuplicatesTest() throws Exception {
        executeQueryAndCreateCsv(QUERY_0, "query_find-duplicates-0-results.csv");
//        executeQueryAndCreateCsv(QUERY_1, "query_find-duplicates-1-results.csv");
//        executeQueryAndCreateCsv(QUERY_2, "query_find-duplicates-2-results.csv");
//        executeQueryAndCreateCsv(QUERY_3, "query_find-duplicates-3-results.csv");
//        executeQueryAndCreateCsv(QUERY_4, "query_find-duplicates-4-results.csv");
    }


    /**
     * Given a customer that wants to buy a specific car with a fixed budget, how can we help her to take a decision and
     * have a good user experience? We can:
     * <li> - Offer alternatives based on: car categories and previous user decisions with similar search criteria, etc.
     * We've called <b>make categories</b> to this indicator </li>
     * <li> - Increase the budget based on the experience we've collected about the customers initial search and their
     * final decision. We've call <b>budget increase factor</b> to this indicator.</li>
     * @throws Exception
     */
    @Test
    public void extractInsightsByMakeAndFixedBudgetTest() throws Exception {

        // My search
        String myMake = "''Opel''";
        int myBudget = 5000;

        // factor for budgets between 3000 and 6500
        double budgetIncreaseFactor = 0.25;


        // Make categories example
        Map<String, Integer> makeCategories = new HashMap<String, Integer>(){{
            put("''Opel''", 1);
            put("''Seat''", 1);
            put("''Peugeot''", 1);
            put("''Citroën''", 1);
            put("''Renault''", 1);
            put("''Ford''", 1);

            put("''Mercedes''", 2);
            put("''Audi''", 2);
            put("''Volkswagen''", 2);
            put("''BMW''", 2);
            put("''Volvo''", 2);

            put("''Porsche''", 3);
            put("''Ferrari''", 3);
            put("''Aston Martin''", 3);
        }};


        int myValue = makeCategories.get(myMake);
        Set altMakes = CarsMapUtils.getKeysByValue(makeCategories, myValue);

        String query_insights =
        "SELECT * " +
        "FROM " + TABLE_NAME + " " +
        "WHERE make in (" + CarsMapUtils.formatCollection(altMakes) + ") and " +
              "price <= " + myBudget*(1+budgetIncreaseFactor);

        // execute
        executeQueryAndCreateCsv(query_insights, "query_insights.csv");

    }


    /**
     * Util class to execute a SQL query through the CSVWRITE H2 call and create a CSV file with the output
     * @param query Query to execute
     * @param fileName File name to create the CSV file
     * @throws Exception
     */
    private void executeQueryAndCreateCsv(String query, String fileName) throws Exception {
        int loadedRows = -1;

        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection(H2_CONNECTION, "", "");
        stmt = conn.createStatement();

        LOGGER.info("Executing query: " + query + "\nSaving query in: " + fileName);
        stmt.executeUpdate("CALL CSVWRITE('" + fileName + "','" + query +"','charset=UTF-8 fieldSeparator=,')");
//        stmt.execute("DELETE FROM cars WHERE uniqueId = (")
    }


    /**
     * Execute an update SQL sentence
     * @param query Query to execute
     * @throws Exception
     */
    private void executeUpdateQuery(String query) throws Exception {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection(H2_CONNECTION, "", "");
        stmt = conn.createStatement();

        stmt.executeUpdate(query);
    }
}

