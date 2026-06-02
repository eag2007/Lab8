package org.example.gui.managers;

import org.example.packet.collection.Coordinates;
import org.example.packet.collection.Location;
import org.example.packet.collection.RouteClient;

import java.math.BigDecimal;
import java.util.List;

public class ManagerValidation {

    /**
     * Валидация данных из скрипта
     * @param values - Список строк
     * @return - объект RouteClient
     */
    public RouteClient validateFromScript(List<String> values) {
        String[] labels = {
                "name", "X", "Y",
                "From X", "From Y", "From Z",
                "To X", "To Y", "To Z",
                "distance", "price"
        };

        if (values == null) {
            throw new IllegalArgumentException("В скрипте отсутствуют данные для маршрута");
        }

        String[] v = new String[11];
        for (int i = 0; i < 11; i++) {
            String line = i < values.size() ? values.get(i) : null;
            if (line == null) {
                throw new IllegalArgumentException(
                        "В скрипте не хватает данных для маршрута: отсутствует поле '" + labels[i] + "'");
            }
            v[i] = line;
        }

        return validateFromFields(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10]);
    }


    /**
     * Считывание данных с полей TextField
     * Параметры являются характеристиками класса RouteClient
     * @param name
     * @param coordX
     * @param coordY
     * @param fromX
     * @param fromY
     * @param fromZ
     * @param toX
     * @param toY
     * @param toZ
     * @param distance
     * @param price
     * @return
     */
    public RouteClient validateFromFields(String name, String coordX, String coordY,
                                          String fromX, String fromY, String fromZ,
                                          String toX, String toY, String toZ,
                                          String distance, String price) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("NAME не может быть пустым");
        }
        if (name.trim().length() > 255) {
            throw new IllegalArgumentException("NAME не может быть длиннее 255 символов");
        }

        long x;
        try {
            x = Long.parseLong(coordX.trim());
            if (x <= 0) {
                throw new IllegalArgumentException("X должен быть положительным (больше 0)");
            }
            if (x > 108) {
                throw new IllegalArgumentException("X должен быть не больше 108");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("X должен быть целым положительным числом (от 1 до 108)");
        }

        long y;
        try {
            y = Long.parseLong(coordY.trim());
            if (y <= 0) {
                throw new IllegalArgumentException("Y должен быть положительным (больше 0)");
            }
            if (y > 20) {
                throw new IllegalArgumentException("Y должен быть не больше 20");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Y должен быть целым положительным числом (от 1 до 20)");
        }
        Coordinates coordinates = new Coordinates(x, y);

        float fromXf;
        try {
            fromXf = Float.parseFloat(fromX.trim());
            if (fromXf <= 0) {
                throw new IllegalArgumentException("From X должен быть положительным (больше 0)");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("From X должен быть положительным числом");
        }

        Double fromYd;
        try {
            fromYd = Double.parseDouble(fromY.trim());
            if (fromYd <= 0) {
                throw new IllegalArgumentException("From Y должен быть положительным (больше 0)");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("From Y должен быть положительным числом");
        }

        Integer fromZi;
        try {
            fromZi = Integer.parseInt(fromZ.trim());
            if (fromZi <= 0) {
                throw new IllegalArgumentException("From Z должен быть положительным (больше 0)");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("From Z должен быть целым положительным числом");
        }
        Location locationFrom = new Location(fromXf, fromYd, fromZi);

        float toXf;
        try {
            toXf = Float.parseFloat(toX.trim());
            if (toXf <= 0) {
                throw new IllegalArgumentException("To X должен быть положительным (больше 0)");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("To X должен быть положительным числом");
        }

        Double toYd;
        try {
            toYd = Double.parseDouble(toY.trim());
            if (toYd <= 0) {
                throw new IllegalArgumentException("To Y должен быть положительным (больше 0)");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("To Y должен быть положительным числом");
        }

        Integer toZi;
        try {
            toZi = Integer.parseInt(toZ.trim());
            if (toZi <= 0) {
                throw new IllegalArgumentException("To Z должен быть положительным (больше 0)");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("To Z должен быть целым положительным числом");
        }
        Location locationTo = new Location(toXf, toYd, toZi);

        int distanceInt;
        try {
            distanceInt = Integer.parseInt(distance.trim());
            if (distanceInt <= 1) {
                throw new IllegalArgumentException("Distance должен быть больше 1 (минимальное значение 2)");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Distance должен быть целым положительным числом");
        }

        BigDecimal priceBd;
        try {
            priceBd = new BigDecimal(price.trim().replace(',', '.'));
            if (priceBd.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Price не может быть отрицательным");
            }
            if (priceBd.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Price должен быть больше 0");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Price должен быть положительным числом");
        }

        return new RouteClient(name, coordinates, locationFrom, locationTo, distanceInt, priceBd);
    }
}