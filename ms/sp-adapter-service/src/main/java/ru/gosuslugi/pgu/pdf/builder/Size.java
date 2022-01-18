package ru.gosuslugi.pgu.pdf.builder;

import java.util.Objects;

/**
 * Класс указания размеров
 * Нужен для вычисления относительных размеров как класс параметр
 */
final class Size {
    /** Высота */
    final float height;
    /** Ширина */
    final float width;

    /**
     * Создание экземпляра через указание размеров
     * @param width ширина
     * @param height высота
     */
    public Size(float width, float height) {
        this.height = height;
        this.width = width;
    }

    @Override
    public String toString() {
        return "Size{" +
                "height=" + height +
                ", width=" + width +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Size size = (Size) o;
        return Float.compare(size.height, height) == 0 && Float.compare(size.width, width) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, width);
    }
}