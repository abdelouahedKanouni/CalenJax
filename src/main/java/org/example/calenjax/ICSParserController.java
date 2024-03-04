package org.example.calenjax;


import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Set;

public class ICSParserController {

    public List<Event> parse(String filePath)  {
        // Reading the file and creating the calendar
        try {
            System.out.println(HelloApplication.class.getResource("test.ics"));
            System.out.println("chaussure0");

            FileInputStream fin = new FileInputStream("C:/Users/romai/Documents/M1/semestre2/serveur_app/CalenJax/target/classes/org/example/calenjax/test.ics");
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);

            System.out.println("chaussure5");
            try {
                // Create the date range which is desired.
                DateTime from = new DateTime("20100101T070000Z");
                DateTime to = new DateTime("20100201T070000Z");;
                Period period = new Period(from.toInstant(), to.toInstant());


                System.out.println("chaussure");
                // For each VEVENT in the ICS
                for (Object o : calendar.getComponents("VEVENT")) {
                    Component c = (Component)o;
                    Set<Period<Temporal>> list = c.calculateRecurrenceSet(period);

                    for (Object po : list) {
                        System.out.println((Period)po);
                    }
                }
            }catch (ParseException e){
                e.printStackTrace();
            }

        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }

        System.out.println("chaussure1");




        return null;
    }


}
