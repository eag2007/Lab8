package org.example.gui.managers;

import org.example.packet.collection.Coordinates;
import org.example.packet.collection.Location;
import org.example.packet.collection.RouteClient;

import java.math.BigDecimal;
import java.util.List;

public class ManagerValidation {

    public RouteClient validateFromScript(List<String> values) {
        String[] labels = {"name", "X", "Y", "From X", "From Y", "From Z", "To X", "To Y", "To Z", "distance", "price"};

        if (values == null) throw new IllegalArgumentException("В скрипте отсутствуют данные для маршрута");

        String[] v = new String[11];
        for (int i = 0; i < 11; i++) {
            String line = i < values.size() ? values.get(i) : null;
            if (line == null) throw new IllegalArgumentException("В скрипте не хватает поля '" + labels[i] + "'");
            v[i] = line;
        }

        return validateFromFields(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10]);
    }

    public RouteClient validateFromFields(String name, String coordX, String coordY,
                                          String fromX, String fromY, String fromZ,
                                          String toX, String toY, String toZ,
                                          String distance, String price) {

        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException(ManagerLanguage.get("error.name.empty"));
        if (name.trim().length() > 255) throw new IllegalArgumentException(ManagerLanguage.get("error.name.length"));

        long x;
        try {
            x = Long.parseLong(coordX.trim());
            if (x <= 0 || x > 108) throw new IllegalArgumentException(ManagerLanguage.get("error.coord_x.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.coord_x.invalid"));
        }

        long y;
        try {
            y = Long.parseLong(coordY.trim());
            if (y <= 0 || y > 20) throw new IllegalArgumentException(ManagerLanguage.get("error.coord_y.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.coord_y.invalid"));
        }
        Coordinates coordinates = new Coordinates(x, y);

        float fromXf;
        try {
            fromXf = Float.parseFloat(fromX.trim());
            if (fromXf <= 0) throw new IllegalArgumentException(ManagerLanguage.get("error.from_x.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.from_x.invalid"));
        }

        Double fromYd;
        try {
            fromYd = Double.parseDouble(fromY.trim());
            if (fromYd <= 0) throw new IllegalArgumentException(ManagerLanguage.get("error.from_y.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.from_y.invalid"));
        }

        Integer fromZi;
        try {
            fromZi = Integer.parseInt(fromZ.trim());
            if (fromZi <= 0) throw new IllegalArgumentException(ManagerLanguage.get("error.from_z.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.from_z.invalid"));
        }
        Location locationFrom = new Location(fromXf, fromYd, fromZi);

        float toXf;
        try {
            toXf = Float.parseFloat(toX.trim());
            if (toXf <= 0) throw new IllegalArgumentException(ManagerLanguage.get("error.to_x.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.to_x.invalid"));
        }

        Double toYd;
        try {
            toYd = Double.parseDouble(toY.trim());
            if (toYd <= 0) throw new IllegalArgumentException(ManagerLanguage.get("error.to_y.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.to_y.invalid"));
        }

        Integer toZi;
        try {
            toZi = Integer.parseInt(toZ.trim());
            if (toZi <= 0) throw new IllegalArgumentException(ManagerLanguage.get("error.to_z.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.to_z.invalid"));
        }
        Location locationTo = new Location(toXf, toYd, toZi);

        int distanceInt;
        try {
            distanceInt = Integer.parseInt(distance.trim());
            if (distanceInt <= 1) throw new IllegalArgumentException(ManagerLanguage.get("error.distance.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.distance.invalid"));
        }

        BigDecimal priceBd;
        try {
            priceBd = new BigDecimal(price.trim().replace(',', '.'));
            if (priceBd.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException(ManagerLanguage.get("error.price.invalid"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ManagerLanguage.get("error.price.invalid"));
        }

        return new RouteClient(name, coordinates, locationFrom, locationTo, distanceInt, priceBd);
    }
}
