package org.example.calenjax.Controlleurs;


import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.predicate.PeriodRule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Period;
import org.example.calenjax.Event;
import org.example.calenjax.HelloApplication;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class ICSParserController {

    public List<Event> parse(String filePath, String FormData, LocalDateTime date)  {
        try {
            System.out.println(HelloApplication.class.getResource("test.ics"));

            FileInputStream fin = new FileInputStream(filePath);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);

            Filter filter = getFilter(date);

            List eventsToday = (List) filter.filter(calendar.getComponents(Component.VEVENT));
            List events = new ArrayList<>();
            for (Object c : eventsToday){
                System.out.println(c);
                Event e = convertToEvent((Component) c);
                if (e != null){
                    events.add(e);
                }
            }
            return events;

        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }

        return null;
    }

    @NotNull
    private static Filter getFilter(LocalDateTime now) {
        LocalDate startOfWeek = now.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = now.toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime startOfWeekDateTime = LocalDateTime.of(startOfWeek, LocalTime.MIN);
        LocalDateTime endOfWeekDateTime = LocalDateTime.of(endOfWeek, LocalTime.MAX);

        Period period = new Period(startOfWeekDateTime, endOfWeekDateTime);
        Filter filter = new Filter(new PeriodRule(period));
        return filter;
    }


    private Event convertToEvent(Component component)
    {
        String uid = "";
        String summary = "";
        String description = "";
        String location = "";

        String dtstart = "";
        String dtend = "";
        for (final Property property : component.getProperties()) {
            switch (property.getName()) {
                case "SUMMARY" -> summary = property.getValue();
                case "DESCRIPTION" -> description = property.getValue();
                case "UID" -> uid = property.getValue();
                case "LOCATION" -> location = property.getValue();
                case "DTSTART" -> dtstart = property.getValue();
                case "DTEND" -> dtend = property.getValue();
            }
        }
        System.out.println("dt  " + (dtstart) + " " + (dtend));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd['T'HHmmssX]");

        LocalDateTime dateTimeStart = parseWithDefaultTime(dtstart, formatter);
        LocalDateTime dateTimeEnd = parseWithDefaultTime(dtend, formatter);

        System.out.println("Start Date: " + dateTimeStart);
        System.out.println("End Date: " + dateTimeEnd);

        int hourStart = dateTimeStart.getHour() + 1;
        int minuteStart = dateTimeStart.getMinute();

        int hourEnd = dateTimeEnd.getHour() + 1;
        int minuteEnd = dateTimeEnd.getMinute();

        String dayName = dateTimeStart.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        int startRow = (hourStart - 8) * 2;
        if (minuteStart == 30){startRow++;}
        int rowSpan = (hourEnd - 8) * 2 - startRow;
        if (minuteEnd == 30){rowSpan++;}
        int startCol = switch (dayName) {
            case "lundi" -> 1;
            case "mardi" -> 2;
            case "mercredi" -> 3;
            case "jeudi" -> 4;
            case "vendredi" -> 5;
            default -> 0;
        };
        if (dateTimeEnd.getDayOfYear() != dateTimeStart.getDayOfYear()){
            startRow = 0;
            rowSpan = 25;
        }
        System.out.println("deb " + (startRow + 1) + " " + (rowSpan + 1));
        if (startRow<1){startRow=1;}
        if (startCol == 0){return null;}
        return new Event(uid, summary, description, location, startRow + 1, startCol, rowSpan);
    }

    private static LocalDateTime parseWithDefaultTime(String input, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(input, formatter);
        } catch (Exception e) {
            return LocalDateTime.parse(input + "T000000Z", formatter);
        }
    }


}