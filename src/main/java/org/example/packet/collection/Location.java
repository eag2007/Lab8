package org.example.packet.collection;

import java.io.Serializable;

/**
 * Класс координат одной точки маршрута
 */
public class Location implements Comparable<Location>, Serializable {
    private float x;
    private Double y;
    private int z;

    /**
     * Сравнивает точки по координатам X, Y и Z
     *
     * @param o - точка для сравнения
     * @return результат сравнения точек
     */
    @Override
    public int compareTo(Location o) {
        int xCompare = Float.compare(this.x, o.x);
        if (xCompare != 0) {
            return xCompare;
        }

        int yCompare = Double.compare(this.y, o.y);
        if (yCompare != 0) {
            return yCompare;
        }

        return Integer.compare(this.z, o.z);
    }

    /**
     * Создаёт точку с координатами
     *
     * @param x - координата X
     * @param y - координата Y
     * @param z - координата Z
     */
    public Location(float x, Double y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Получить координату X
     *
     * @return координата X
     */
    public float getX() {
        return this.x;
    }

    /**
     * Получить координату Y
     *
     * @return координата Y
     */
    public Double getY() {
        return this.y;
    }

    /**
     * Получить координату Z
     *
     * @return координата Z
     */
    public int getZ() {
        return this.z;
    }
}
