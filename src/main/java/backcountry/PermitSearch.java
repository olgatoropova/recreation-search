package backcountry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class PermitSearch extends RecreationSearch {
    // date format 2023-05-01T00:00:00.000Z
    // enchantment permit id:  233273
    // ncnp permit id: 4675322

    public static final int ENCHANTMENTS_PERMIT_ID = 233273;

    public static final int[] ENCHANTMENTS_ZONES_IDS = new int[] {
            23, // snow zone 23
            27, // stuart
            28, // eightmile
            29, // colchuck
            30, // core
    };
    private static final String BASE_URL = "https://www.recreation.gov/api/permits/%s/availability/month?start_date=%s";

    public PermitSearch(int permitId, int[] zonesIds, Month startMonth) {
        super(permitId, zonesIds, startMonth);
    }

    String getURL(LocalDateTime startDate) {
        String formattedDate = DATE_TIME_MS_FORMATTER.format(startDate);
        try {
            String url = String.format(BASE_URL, placeId, URLEncoder.encode(formattedDate, "UTF-8"));
            System.out.println(url);
            return url;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    Map<String, List<LocalDate>> parseJSON(Reader reader) throws IOException, ParseException {

        Map<String, List<LocalDate>> result = new HashMap<>();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(reader);

        JSONObject payload = (JSONObject) jsonObject.get("payload");
        JSONObject zones = (JSONObject) payload.get("availability");

        for (int zoneId : zonesIds) {
            JSONObject zone = (JSONObject) zones.get("" + zoneId);

            Set<Map.Entry<String, JSONObject>> availabilities = ((JSONObject) zone.get("date_availability")).entrySet();

            List<LocalDate> availableDates = new ArrayList<>();
            for (Map.Entry<String, JSONObject> availability : availabilities) {
                Long remaining = (Long)availability.getValue().get("remaining");
                if (remaining > 0) {
                    availableDates.add(LocalDate.parse(availability.getKey(), DATE_TIME_FORMATTER));
                }
            }

            if (!availableDates.isEmpty()) {
                availableDates.sort(Comparator.naturalOrder());

                result.put("" + zoneId, availableDates);
            }
        }

        return result;
    }
}
