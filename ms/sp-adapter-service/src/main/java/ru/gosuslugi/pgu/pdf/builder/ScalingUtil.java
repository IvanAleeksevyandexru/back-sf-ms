package ru.gosuslugi.pgu.pdf.builder;

/**
 * Утилита вычисления растягивания файла горизонталь / вертикаль
 */
class ScalingUtil {
    private ScalingUtil() { }

    /**
     * Вычисление размера изображения максимально растянутого по вертикали и горизонтали с учетом отношения сторон.
     * Все размеры задаются в относительных единицах измерения, однако результат будет представлен в единицах измерения
     * области отображения.
     * @param imageSize размер изображения в относительных единицах
     * @param boxSize размер области отображения в относительных единицах
     * @return размер изображения в относительных единицах области отображения
     */
    public static Size calcImageSize(Size imageSize, Size boxSize) {
        //вычисляем отношение ширины к высоте для определения масштабирования
        float boxRatio = boxSize.width / boxSize.height;
        float imageRatio = imageSize.width / imageSize.height;

        //определения способа масштабирования
        ScalingType scalingType = boxRatio > imageRatio ? ScalingType.VERTICAL : ScalingType.HORIZONTAL;
        return getScalingSize(imageSize, boxSize, scalingType);
    }

    /**
     * Определение размеров по заданному типу
     * @param imageSize размер изображения
     * @param boxSize размер области вывода
     * @param scalingType тип растягивания
     * @return размер изображения растянутого по указанному алгоритму
     */
    private static Size getScalingSize(Size imageSize, Size boxSize, ScalingType scalingType) {
        switch (scalingType) {
            case HORIZONTAL: return new Size(boxSize.width, imageSize.height * boxSize.width / imageSize.width);
            case VERTICAL: return new Size(imageSize.width * boxSize.height / imageSize.height, boxSize.height);
            default: return boxSize;
        }
    }
}
