package backcountry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;


public class CampsiteSearch extends RecreationSearch {

    public static final int KALALOCH_CAMP_ID = 232464;
    public static final int BUMPING_LAKE_CAMP_ID = 232356;

    public static final int[] KALALOCH_CAMPSITES_IDS = new int[] {
            //3152, // A057
            3203, // A022
            3102, // A023
            3104, // A025
            3117 // A027
    };

    public static final int[] BUMPING_LAKE_CAMPSITES_IDS = new int[] {
            81693, // 043
            81668, // 044
            81923, // 045
    };

    // 2023-08-01T00%3A00%3A00.000Z
    private static final String BASE_URL = "https://www.recreation.gov/api/camps/availability/campground/%s/month?start_date=%s";

    public CampsiteSearch(int campId, int[] campsitesIds, Month startMonth) {
        super(campId, campsitesIds, startMonth);
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

        JSONObject campsites = (JSONObject) jsonObject.get("campsites");

        for (int siteId : zonesIds) {
            JSONObject campsite = (JSONObject) campsites.get("" + siteId);
            String siteName = (String) campsite.get("site");

            Set<Map.Entry<String,String>> availabilities = ((JSONObject) campsite.get("availabilities")).entrySet();

            List<LocalDate> availableDates = new ArrayList<>();
            for (Map.Entry<String,String> availability : availabilities) {
                if (availability.getValue().equals("Available")) {
                    availableDates.add(LocalDate.parse(availability.getKey(), DATE_TIME_FORMATTER));
                }
            }

            if (!availableDates.isEmpty()) {
                availableDates.sort(Comparator.naturalOrder());

                result.put(siteName, availableDates);
            }
        }

        return result;
    }
}
