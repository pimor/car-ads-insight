package util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Cars;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created on 25/09/2017.
 */
public class JsonCarsToCsv {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonCarsToCsv.class);

    public static final String CAR_FIELDS_SCHEMA = "country,uniqueId,urlAnonymized,make,model,year,mileage,price,doors," +
            "fuel,carType,transmission,color,region,city,date,titleChunk,contentChunk";


    /**
     * Parse input JSON into the {@link Cars} object
     * @param jsonLine One JSON line
     * @return the {@link Cars} object with the data from the JSON
     */
    public static Cars parseFromJsonLine(String jsonLine) {
        ObjectMapper mapper = new ObjectMapper();
        Cars cars = null;

        try {
            cars = mapper.readValue(jsonLine, Cars.class);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cars;
    }


    /**
     * Transform input json file into a CSV file
     * @param sc
     * @param logFile
     * @param outputDirName
     */
    public void transformJsonIntoCsv(JavaSparkContext sc, String logFile, File outputDirName) {
        JavaRDD<String> apiLogLines = sc.textFile(logFile);

        LOGGER.info("Converting input JSON line into a Spark RDD...");
        // Convert the log lines to ApiLog objects
        JavaRDD<Cars> accessLogs =
                apiLogLines.map(JsonCarsToCsv::parseFromJsonLine).cache();

        LOGGER.info("Saving CSV file in " + outputDirName.getAbsolutePath());
        accessLogs.saveAsTextFile(outputDirName.getAbsolutePath());
    }


    /**
     * Load a CSV file into a local H2 database
     * @param outputCsvDir Directory where are the files to load
     * @return Number of loaded rows
     * @throws Exception
     */
    public int loadCsvIntoH2(File outputCsvDir, String connection, String user, String password, String tableName,
                             String csvSchema) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        int loadedRows = -1;

        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection(connection, user, password);
        stmt = conn.createStatement();


        stmt.execute("CREATE TABLE " + tableName + " ( " +
                "country VARCHAR," +
                "uniqueId VARCHAR," +
                "urlAnonymized VARCHAR," +
                "make VARCHAR," +
                "model VARCHAR," +
                "year VARCHAR," +
                "mileage VARCHAR," +
                "price VARCHAR," +
                "doors VARCHAR," +
                "fuel VARCHAR," +
                "carType VARCHAR," +
                "transmission VARCHAR," +
                "color VARCHAR," +
                "region VARCHAR," +
                "city VARCHAR," +
                "date VARCHAR," +
                "titleChunk VARCHAR," +
                "contentChunk VARCHAR" +
            ")"
        );


        for(File file : getOutputSparkFiles(outputCsvDir)) {
            stmt.execute("INSERT INTO " + tableName + " SELECT * FROM CSVREAD('" +
                    file.getAbsolutePath() + "', '" +
                    csvSchema + "', " +
                    "'charset=UTF-8 fieldSeparator=,'" +
                ")");
        }

        ResultSet rs = stmt.executeQuery("SELECT count(1) c from " + tableName);

        while( rs.next() ) {
            loadedRows = rs.getInt("c");
            LOGGER.info("Loaded " + loadedRows + " rows.");
        }

        return loadedRows;
    }


    /**
     * Get the files generated by the Spark process
     * @param outputApiLogFile Directory where the Spark process writes
     * @return The list of generated files (we assume that the file prefix is part-*)
     */
    private File[] getOutputSparkFiles(File outputApiLogFile) {
        File dir = new File(outputApiLogFile.getAbsolutePath());
        File [] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("part-");
            }
        });

        return files;
    }

}
