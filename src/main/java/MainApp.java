import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

class MainApp {
    public static void main(String[] args) throws IOException, ParseException {
        JSONParser parser = new JSONParser();

        JSONObject jsonTickets = (JSONObject) parser.parse(new InputStreamReader( new FileInputStream("tickets.json"), StandardCharsets.UTF_8));

        ArrayList<Integer> minOnWayArray = new ArrayList<>();
        int averageFlightTime = 0;

        for (Object o: (ArrayList) jsonTickets.get("tickets")) {
            JSONObject ticket = (JSONObject) o;

            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd.MM.yy");

            LocalDate departureDate = LocalDate.parse(ticket.get("departure_date").toString(), formatterDate);
            LocalDate arrivalDate = LocalDate.parse(ticket.get("arrival_date").toString(), formatterDate);

            Period period = Period.between(departureDate, arrivalDate);

            String[] departureTime = ticket.get("departure_time").toString().split(":");
            String[] arrivalTime = ticket.get("arrival_time").toString().split(":");

            int minOnWay =  60 * 24 * period.getDays() - 60 * Integer.parseInt(departureTime[0]) - Integer.parseInt(departureTime[1])
                    + 60 * Integer.parseInt(arrivalTime[0]) + Integer.parseInt(arrivalTime[1]) + 540;

            minOnWayArray.add(minOnWay);
            averageFlightTime += minOnWay;
        }

        averageFlightTime = averageFlightTime / minOnWayArray.size();
        System.out.println("Среднее время в пути: " + averageFlightTime / 60 + ":" + averageFlightTime % 60);

        Collections.sort(minOnWayArray);
        int indexPercentiles = minOnWayArray.size() * 90 / 100;
        System.out.println("90-й процентиль: " + minOnWayArray.get(indexPercentiles - 1) / 60 + ":" + minOnWayArray.get(indexPercentiles - 1) % 60);
    }
}
