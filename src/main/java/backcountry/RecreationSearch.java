package backcountry;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class RecreationSearch {

    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static final DateTimeFormatter DATE_TIME_MS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    // Camp or permit id
    int placeId;

    // Sub ids (campsite or zone)
    int[] zonesIds;

    private final Month startMonth;

    RecreationSearch(int placeId, int[] zonesIds, Month startMonth) {
        this.placeId = placeId;
        this.zonesIds = zonesIds;
        this.startMonth = startMonth;
    }

    abstract Map<String, List<LocalDate>> parseJSON(Reader reader)
            throws IOException, ParseException;

    abstract String getURL(LocalDateTime startDate);

    public int getPlaceId() {
        return placeId;
    }

    public Map<String, List<LocalDate>> getAvailable() {
        LocalDateTime currentDate = LocalDateTime.now();
        Map<String, List<LocalDate>> result = new HashMap<>();

        Month monthRunner = startMonth;
        while (monthRunner.getValue() <= Month.SEPTEMBER.getValue()) {
            LocalDateTime startDate = LocalDateTime.of(
                    currentDate.getYear(), monthRunner, 1, 0, 0);

            Map<String, List<LocalDate>> runnerResult = getAvailable(startDate);
            if (runnerResult != null) {
                for (Map.Entry<String, List<LocalDate>> entry : runnerResult.entrySet()) {

                    List<LocalDate> availableDates = result.get(entry.getKey());
                    if (availableDates != null) {
                        availableDates.addAll(entry.getValue());
                    } else {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            monthRunner = Month.of(monthRunner.getValue() + 1);
        }

        return result;
    }


    private Map<String, List<LocalDate>> getAvailable(LocalDateTime startDate) {
        InputStreamReader reader = null;
        try {
            URL url = new URL(getURL(startDate));
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream input = url.openStream();
            reader = new InputStreamReader(input);

            return parseJSON(reader);

        } catch (IOException | ParseException e) {
            System.out.println(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }

        return null;
    }
}
