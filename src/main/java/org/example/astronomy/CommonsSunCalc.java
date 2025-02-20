package org.example.astronomy;


import org.shredzone.commons.suncalc.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class CommonsSunCalc {

    String dateTimeFormat = "HH:mm";
    //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd hh:mm");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);

    private String sunRiseFormatted;


    private String timezone;
    private double latitude;
    private double longitude;

    private String sunSetFormatted;
    private String moonRiseFormatted;
    private String moonSetFormatted;
    private String sunNadirFormatted;
    private String sunAzimuthFormatted;
    private String sunAltitudeFormatted;
    private String sunDistanceFormatted;
    private String sunNoonFormatted;

    private String moonAzimuthFormatted;
    private String moonAltitudeFormatted;
    private String moonDistanceFormatted;
    private String dayLength;

    private String sunRiseAstronomical;
    private String sunRiseBlueHour;
    private String sunRiseCivil;
    private String sunRiseGoldenHour;
    private String sunRiseHorizon;
    private String sunRiseNautical;
    private String sunRiseNightHour;
    private String sunRiseVisual;
    private String sunRiseVisualLower;
    private String sunSetAstronomical;
    private String sunSetBlueHour;
    private String sunSetCivil;
    private String sunSetGoldenHour;
    private String sunSetHorizon;
    private String sunSetNautical;
    private String sunSetNightHour;
    private String sunSetVisual;
    private String sunSetVisualLower;

    private boolean isSunAlwaysUp;
    private boolean isSunAlwaysDown;

    public CommonsSunCalc(String timezone, double latitude, double longitude) {
        this.timezone = timezone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAstronomy() {

        //https://shredzone.org/maven/commons-suncalc/examples.html
        StringBuilder stringBuilder = new StringBuilder();

        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        LocalTime currentTime = LocalTime.now();
        int hour = currentTime.getHour();
        int minute = currentTime.getMinute();
        int second = currentTime.getSecond();
/*
        String[] latParts = latitudeStr.split("[^\\d]+");
        String[] longParts = longitudeStr.split("[^\\d]+");
        //System.out.println(latParts);
        int latitudeDegrees = Integer.parseInt(latParts[0]);
        int latitudeMinutes = Integer.parseInt(latParts[1]);
        double latitudeSeconds = Double.parseDouble(latParts[2]);

        int longitudeDegrees = Integer.parseInt(longParts[0]);
        int longitudeMinutes = Integer.parseInt(longParts[1]);
        double longitudeSeconds = Double.parseDouble(longParts[2]);
*/
        // SUN


        SunPosition.Parameters sunParam = SunPosition.compute()
               // .latitude(latitudeDegrees, latitudeMinutes, latitudeSeconds)
               // .longitude(longitudeDegrees, longitudeMinutes, longitudeSeconds)
                .latitude(latitude)
                .longitude(longitude)
                .timezone(timezone)
                .on(LocalDateTime.now());
        //.on(2018, 11, 13, 10, 3, 24);   // 2018-11-13 10:03:24


        SunPosition sun = sunParam.execute();
        /*
        System.out.println(String.format(
                "The sun can be seen %.1f° clockwise from the North and "
                        + "%.1f° above the horizon.\nIt is about %.0f km away right now.",
                sun.getAzimuth(),
                sun.getAltitude(),
                sun.getDistance()
        ));

         */

        sunAltitudeFormatted = String.format("%.1f° above the horizon", sun.getAltitude());
        sunAzimuthFormatted = String.format("%.1f° clockwise from the North", sun.getAzimuth());
        sunDistanceFormatted = String.format("%.0f km", sun.getDistance());


        SunTimes sunTimes = SunTimes.compute()
                .on(year, month, day)    // starting midnight
                .timezone(timezone)
                .latitude(latitude)
                .longitude(longitude)
                .oneDay()
                .execute();

        sunRiseAstronomical = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.ASTRONOMICAL).getRise().format(dateTimeFormatter);
        sunSetAstronomical = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.ASTRONOMICAL).getSet().format(dateTimeFormatter);

        sunRiseNautical = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.NAUTICAL).getRise().format(dateTimeFormatter);
        sunSetNautical = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.NAUTICAL).getSet().format(dateTimeFormatter);

        sunRiseVisual = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.VISUAL).getRise().format(dateTimeFormatter);
        sunSetVisual = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.VISUAL).getSet().format(dateTimeFormatter);

        sunRiseCivil = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.CIVIL).getRise().format(dateTimeFormatter);
        sunSetCivil = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.CIVIL).getSet().format(dateTimeFormatter);

        sunRiseBlueHour = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.BLUE_HOUR).getRise().format(dateTimeFormatter);
        sunSetBlueHour = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.BLUE_HOUR).getSet().format(dateTimeFormatter);

        sunRiseGoldenHour = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.GOLDEN_HOUR).getRise().format(dateTimeFormatter);
        sunSetGoldenHour = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.GOLDEN_HOUR).getSet().format(dateTimeFormatter);

        sunRiseHorizon = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.HORIZON).getRise().format(dateTimeFormatter);
        sunSetHorizon = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.HORIZON).getSet().format(dateTimeFormatter);

        sunRiseNightHour = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.NIGHT_HOUR).getRise().format(dateTimeFormatter);
        sunSetNightHour = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.NIGHT_HOUR).getSet().format(dateTimeFormatter);

        sunRiseVisualLower = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.VISUAL_LOWER).getRise().format(dateTimeFormatter);
        sunSetVisualLower = getSunTimesWithTwilight(sunParam, SunTimes.Twilight.VISUAL_LOWER).getSet().format(dateTimeFormatter);

        stringBuilder.append("\n");
        //System.out.println(sunTimes.getRise());
        stringBuilder.append("Gündoğumu: " + sunTimes.getRise());
        stringBuilder.append("\n");

        stringBuilder.append("Günbatımı:  " + sunTimes.getSet());

        stringBuilder.append("\n");


        //  System.out.println("nadir: " + sunTimes.getNadir());
        //  System.out.println("noon: " + sunTimes.getNoon());

        // MOON


        MoonPosition.Parameters moonParam = MoonPosition.compute()
                .sameLocationAs(sunParam)
                .sameTimeAs(sunParam);

        MoonPosition moon = moonParam.execute();
        moonAltitudeFormatted = String.format("%.1f° above the horizon", moon.getAltitude());
        moonAzimuthFormatted = String.format("%.1f° clockwise from the North", moon.getAzimuth());
        moonDistanceFormatted = String.format("%.0f km", moon.getDistance());

        MoonTimes moonTimes = MoonTimes.compute()
                .on(year, month, day)    // starting midnight
                .timezone(timezone)
                .latitude(latitude)
                .longitude(longitude)
                .execute();

        stringBuilder.append("\n");

        stringBuilder.append("Aydoğumu: " + moonTimes.getRise());
        stringBuilder.append("\n");

        stringBuilder.append("Aybatımı:  " + moonTimes.getSet());
        stringBuilder.append("\n");


        // Saat bazında fark
        long hours = ChronoUnit.HOURS.between(sunTimes.getRise(), sunTimes.getSet());
        // Dakika bazında fark
        long totalMinutes = ChronoUnit.MINUTES.between(sunTimes.getRise(), sunTimes.getSet());

        // Örneğin, sadece "saat + dakika" formatı isterseniz:
        long remainingMinutes = totalMinutes % 60;


        dayLength = String.format("%s hours %s minutes", hours, remainingMinutes);

        sunRiseFormatted = sunTimes.getRise().format(dateTimeFormatter);
        sunSetFormatted = sunTimes.getSet().format(dateTimeFormatter);
        moonRiseFormatted = moonTimes.getRise().format(dateTimeFormatter);
        moonSetFormatted = moonTimes.getSet().format(dateTimeFormatter);

        sunNadirFormatted = sunTimes.getNadir().format(dateTimeFormatter);
        sunNoonFormatted = sunTimes.getNoon().format(dateTimeFormatter);

        isSunAlwaysUp = sunTimes.isAlwaysUp();
        isSunAlwaysDown = sunTimes.isAlwaysDown();




        /*
        solarSystem = new SolarSystem(sunTimes.getRise(),
                sunTimes.getSet(),
                moonTimes.getRise(),
                moonTimes.getSet(),
                sunRiseFormatted,
                sunSetFormatted,
                moonRiseFormatted,
                moonSetFormatted
        );
*/
        // MOON POSITON

        MoonPosition moonPosition = MoonPosition.compute()
                .on(year, month, day, hour, minute, second)    // starting midnight
                .timezone(timezone)
                .latitude(latitude)
                .longitude(longitude)
                .execute();
        stringBuilder.append("\n");

        stringBuilder.append(String.format(
                "The moon can be seen %.1f° clockwise from the North and "
                        + "%.1f° above the horizon.\nIt is about %.0f km away right now.",
                moonPosition.getAzimuth(),
                moonPosition.getAltitude(),
                moonPosition.getDistance()));
        stringBuilder.append("\n");

        double azimuth = moonPosition.getAzimuth();
        double altitude = moonPosition.getAltitude();
        double trueAltitude = moonPosition.getTrueAltitude();
        double distance = moonPosition.getDistance();
        double angle = moonPosition.getParallacticAngle();

        StringBuilder sbmp = new StringBuilder();

        sbmp.append("\n");
        sbmp.append("azimuth");
        sbmp.append("\t");
        sbmp.append(azimuth);

        sbmp.append("\n");
        sbmp.append("altitude");
        sbmp.append("\t");
        sbmp.append(altitude);

        sbmp.append("\n");
        sbmp.append("trueAltitude");
        sbmp.append("\t");
        sbmp.append(trueAltitude);

        sbmp.append("\n");
        sbmp.append("distance");
        sbmp.append("\t");
        sbmp.append(distance);

        sbmp.append("\n");
        sbmp.append("angle");
        sbmp.append("\t");
        sbmp.append(angle);


        MoonPhase.Phase phase = MoonPhase.Phase.toPhase(moonPosition.getParallacticAngle());
        // Moon phase angle, in degrees. 0 = New Moon, 180 = Full Moon. Angles outside the [0,360) range are normalized into that range.
        String phaseName = phase.toString();


        sbmp.append("\n");
        sbmp.append("phaseName");
        sbmp.append("\t");
        sbmp.append(phaseName);

        //System.out.println(sbmp);


        // MOON PHASE

        MoonPhase moonPhase = MoonPhase.compute()
                .on(year, month, day, hour, minute, second)    // starting midnight
                .timezone(timezone)
                .execute();
        //  System.out.println(moonPhase.getTime());

        //LocalDate nextFullMoon = moonPhase.getTime().toLocalDate();

        //System.out.println( moonPhase.getTime());


        stringBuilder.append("<br />Ay Fazı:  " + moonPhase);
        stringBuilder.append("\n");

        //  stringBuilder.append("<br />Ay Fazı:  " + phaseName);

        stringBuilder.append("\n");

        MoonIllumination.Parameters parameters = MoonIllumination.compute()
                .on(2024, 3, 26, 21, 21, 00);

        for (int i = 1; i <= 31; i++) {
            long percent = Math.round(parameters.execute().getFraction() * 100.0);
            //System.out.println("On March " + i + " the moon was " + percent + "% lit.");
            parameters.plusDays(1);
        }

        double moonage = 29.0 * (angle / 360.0) + 1.0;
        String content = "Moon Age: " + moonage;
        //System.out.println(content);

        double oneMoonCycleAsDays = 29.53059;
        return stringBuilder.toString();
    }


    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void testMoonPhase() {
        LocalDate date = LocalDate.of(2024, 1, 1);

        MoonPhase.Parameters parameters = MoonPhase.compute()
                .phase(MoonPhase.Phase.FULL_MOON);

        while (true) {
            MoonPhase moonPhase = parameters
                    .on(date)
                    .execute();
            LocalDate nextFullMoon = moonPhase
                    .getTime()
                    .toLocalDate();
            if (nextFullMoon.getYear() == 2025) {
                break;      // we've reached the next year
            }

            //System.out.print(nextFullMoon);
            if (moonPhase.isMicroMoon()) {
                //System.out.print(" (micromoon)");
            }
            if (moonPhase.isSuperMoon()) {
                //System.out.print(" (supermoon)");
            }
            //System.out.println();

            date = nextFullMoon.plusDays(1);
        }
    }

    /*
    https://shredzone.org/maven/commons-suncalc-v2/usage.html#twilight
     */
    private SunTimes getSunTimesWithTwilight(SunPosition.Parameters sunParam, SunTimes.Twilight twilight) {
        SunTimes st = SunTimes.compute()
                .sameLocationAs(sunParam)
                .sameTimeAs(sunParam)
                .twilight(twilight) // default, equals SUNRISE/SUNSET
                .execute();
        return st;
    }

// MoonPhase.compute().phase(MoonPhase.Phase.FULL_MOON);


    public String getSunRiseFormatted() {
        return sunRiseFormatted;
    }

    public void setSunRiseFormatted(String sunRiseFormatted) {
        this.sunRiseFormatted = sunRiseFormatted;
    }

    public String getSunSetFormatted() {
        return sunSetFormatted;
    }

    public void setSunSetFormatted(String sunSetFormatted) {
        this.sunSetFormatted = sunSetFormatted;
    }

    public String getMoonRiseFormatted() {
        return moonRiseFormatted;
    }

    public void setMoonRiseFormatted(String moonRiseFormatted) {
        this.moonRiseFormatted = moonRiseFormatted;
    }

    public String getMoonSetFormatted() {
        return moonSetFormatted;
    }

    public void setMoonSetFormatted(String moonSetFormatted) {
        this.moonSetFormatted = moonSetFormatted;
    }

    public String getSunNadirFormatted() {
        return sunNadirFormatted;
    }

    public void setSunNadirFormatted(String sunNadirFormatted) {
        this.sunNadirFormatted = sunNadirFormatted;
    }

    public String getSunNoonFormatted() {
        return sunNoonFormatted;
    }

    public void setSunNoonFormatted(String sunNoonFormatted) {
        this.sunNoonFormatted = sunNoonFormatted;
    }

    public String getSunAzimuthFormatted() {
        return sunAzimuthFormatted;
    }

    public void setSunAzimuthFormatted(String sunAzimuthFormatted) {
        this.sunAzimuthFormatted = sunAzimuthFormatted;
    }

    public String getSunAltitudeFormatted() {
        return sunAltitudeFormatted;
    }

    public void setSunAltitudeFormatted(String sunAltitudeFormatted) {
        this.sunAltitudeFormatted = sunAltitudeFormatted;
    }

    public String getSunDistanceFormatted() {
        return sunDistanceFormatted;
    }

    public void setSunDistanceFormatted(String sunDistanceFormatted) {
        this.sunDistanceFormatted = sunDistanceFormatted;
    }

    public String getMoonAzimuthFormatted() {
        return moonAzimuthFormatted;
    }

    public void setMoonAzimuthFormatted(String moonAzimuthFormatted) {
        this.moonAzimuthFormatted = moonAzimuthFormatted;
    }

    public String getMoonAltitudeFormatted() {
        return moonAltitudeFormatted;
    }

    public void setMoonAltitudeFormatted(String moonAltitudeFormatted) {
        this.moonAltitudeFormatted = moonAltitudeFormatted;
    }

    public String getMoonDistanceFormatted() {
        return moonDistanceFormatted;
    }

    public void setMoonDistanceFormatted(String moonDistanceFormatted) {
        this.moonDistanceFormatted = moonDistanceFormatted;
    }

    public String getDayLength() {
        return dayLength;
    }

    public void setDayLength(String dayLength) {
        this.dayLength = dayLength;
    }

    public String getSunRiseAstronomical() {
        return sunRiseAstronomical;
    }

    public void setSunRiseAstronomical(String sunRiseAstronomical) {
        this.sunRiseAstronomical = sunRiseAstronomical;
    }

    public String getSunRiseBlueHour() {
        return sunRiseBlueHour;
    }

    public void setSunRiseBlueHour(String sunRiseBlueHour) {
        this.sunRiseBlueHour = sunRiseBlueHour;
    }

    public String getSunRiseCivil() {
        return sunRiseCivil;
    }

    public void setSunRiseCivil(String sunRiseCivil) {
        this.sunRiseCivil = sunRiseCivil;
    }

    public String getSunRiseGoldenHour() {
        return sunRiseGoldenHour;
    }

    public void setSunRiseGoldenHour(String sunRiseGoldenHour) {
        this.sunRiseGoldenHour = sunRiseGoldenHour;
    }

    public String getSunRiseHorizon() {
        return sunRiseHorizon;
    }

    public void setSunRiseHorizon(String sunRiseHorizon) {
        this.sunRiseHorizon = sunRiseHorizon;
    }

    public String getSunRiseNautical() {
        return sunRiseNautical;
    }

    public void setSunRiseNautical(String sunRiseNautical) {
        this.sunRiseNautical = sunRiseNautical;
    }

    public String getSunRiseNightHour() {
        return sunRiseNightHour;
    }

    public void setSunRiseNightHour(String sunRiseNightHour) {
        this.sunRiseNightHour = sunRiseNightHour;
    }

    public String getSunRiseVisual() {
        return sunRiseVisual;
    }

    public void setSunRiseVisual(String sunRiseVisual) {
        this.sunRiseVisual = sunRiseVisual;
    }

    public String getSunRiseVisualLower() {
        return sunRiseVisualLower;
    }

    public void setSunRiseVisualLower(String sunRiseVisualLower) {
        this.sunRiseVisualLower = sunRiseVisualLower;
    }

    public String getSunSetAstronomical() {
        return sunSetAstronomical;
    }

    public void setSunSetAstronomical(String sunSetAstronomical) {
        this.sunSetAstronomical = sunSetAstronomical;
    }

    public String getSunSetBlueHour() {
        return sunSetBlueHour;
    }

    public void setSunSetBlueHour(String sunSetBlueHour) {
        this.sunSetBlueHour = sunSetBlueHour;
    }

    public String getSunSetCivil() {
        return sunSetCivil;
    }

    public void setSunSetCivil(String sunSetCivil) {
        this.sunSetCivil = sunSetCivil;
    }

    public String getSunSetGoldenHour() {
        return sunSetGoldenHour;
    }

    public void setSunSetGoldenHour(String sunSetGoldenHour) {
        this.sunSetGoldenHour = sunSetGoldenHour;
    }

    public String getSunSetHorizon() {
        return sunSetHorizon;
    }

    public void setSunSetHorizon(String sunSetHorizon) {
        this.sunSetHorizon = sunSetHorizon;
    }

    public String getSunSetNautical() {
        return sunSetNautical;
    }

    public void setSunSetNautical(String sunSetNautical) {
        this.sunSetNautical = sunSetNautical;
    }

    public String getSunSetNightHour() {
        return sunSetNightHour;
    }

    public void setSunSetNightHour(String sunSetNightHour) {
        this.sunSetNightHour = sunSetNightHour;
    }

    public String getSunSetVisual() {
        return sunSetVisual;
    }

    public void setSunSetVisual(String sunSetVisual) {
        this.sunSetVisual = sunSetVisual;
    }

    public String getSunSetVisualLower() {
        return sunSetVisualLower;
    }

    public void setSunSetVisualLower(String sunSetVisualLower) {
        this.sunSetVisualLower = sunSetVisualLower;
    }

    public boolean isSunAlwaysUp() {
        return isSunAlwaysUp;
    }

    public void setSunAlwaysUp(boolean sunAlwaysUp) {
        isSunAlwaysUp = sunAlwaysUp;
    }

    public boolean isSunAlwaysDown() {
        return isSunAlwaysDown;
    }

    public void setSunAlwaysDown(boolean sunAlwaysDown) {
        isSunAlwaysDown = sunAlwaysDown;
    }
}
