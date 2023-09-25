package backcountry;

import java.time.Month;

import static backcountry.CampsiteSearch.KALALOCH_CAMP_ID;
import static backcountry.CampsiteSearch.KALALOCH_CAMPSITES_IDS;
import static backcountry.CampsiteSearch.BUMPING_LAKE_CAMP_ID;
import static backcountry.CampsiteSearch.BUMPING_LAKE_CAMPSITES_IDS;
import static backcountry.PermitSearch.ENCHANTMENTS_PERMIT_ID;
import static backcountry.PermitSearch.ENCHANTMENTS_ZONES_IDS;

public class App {
    public static void main( String[] args ) {

        Month month = Month.of(Integer.parseInt(args[0]));
        String sendTo = args[1];

        StringBuilder sb = new StringBuilder("Available sites:\n");

        stringifySearchResults(sb, new CampsiteSearch(KALALOCH_CAMP_ID,
                KALALOCH_CAMPSITES_IDS, month));

        stringifySearchResults(sb, new CampsiteSearch(BUMPING_LAKE_CAMP_ID,
                BUMPING_LAKE_CAMPSITES_IDS, month));

        stringifySearchResults(sb, new PermitSearch(ENCHANTMENTS_PERMIT_ID,
                ENCHANTMENTS_ZONES_IDS, month));

        System.out.println(sb);

        MailSender mailSender = MailSender.getInstance();
        mailSender.send(sendTo,
                "*IMPORTANT INFO* Recreation.gov alert", sb.toString());
    }

    private static void stringifySearchResults(StringBuilder sb, RecreationSearch search) {
        sb.append("Place ").append(search.getPlaceId())
                .append(" = ").append(search.getAvailable()).append('\n');
    }
}
