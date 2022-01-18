package ru.gosuslugi.pgu.sp.adapter.util;

import java.util.Map;
import java.util.Optional;

public class AddressService {


    private static final StringService STRING_SERVICE = new StringService();
    private static final String POINT = ".";

    public String cityAndTown(Map<Object, Object> address) {
        return STRING_SERVICE.printWithDelimiter(
            ", ",
            city(address),
            town(address)
        );
    }

    public String city(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
            " ",
            getMapSafety(address, "city"),
            STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "cityShortType"), POINT)
        );
    }

    public String town(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
            " ",
            getMapSafety(address, "town"),
            STRING_SERVICE.printLeftJoinWithDelimiter( "", getMapSafety(address, "townShortType"), POINT)
        );
    }

    public String district(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
            " ",
            getMapSafety(address, "district"),
            STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "districtShortType"), POINT)
        );
    }

    public String street(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
            " ",
            getMapSafety(address, "street"),
            STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "streetShortType"), POINT)
        );
    }

    public String region(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
            " ",
            getMapSafety(address, "region"),
            STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "regionShortType"), POINT)
        );
    }

    public String house(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
            " ",
            getMapSafety(address, "house"),
            STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "houseShortType"), POINT)
        );
    }

    public String building1AndBuilding2(Map<Object, Object> address) {
        return STRING_SERVICE.printWithDelimiter(
            ", ",
            building1(address),
            building2(address)
        );
    }

    public String building1(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
            " ",
            getMapSafety(address, "building1"),
            STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "building1ShortType"), POINT)
        );
    }

    public String building2(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
            " ",
            getMapSafety(address, "building2"),
            STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "building2ShortType"), POINT)
        );
    }

    public String apartment(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
            " ",
            getMapSafety(address, "apartment"),
            STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "apartmentShortType"), POINT)
        );
    }

    public String inCityDist(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
                " ",
                getMapSafety(address, "inCityDist"),
                STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "inCityDistShortType"), POINT)
        );
    }

    public String additionalArea(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
                " ",
                getMapSafety(address, "additionalArea"),
                STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "additionalAreaShortType"), POINT)
        );
    }

    public String additionalStreet(Map<Object, Object> address) {
        return STRING_SERVICE.printLeftJoinWithDelimiter(
                " ",
                getMapSafety(address, "additionalStreet"),
                STRING_SERVICE.printLeftJoinWithDelimiter("", getMapSafety(address, "additionalStreetShortType"), POINT)
        );
    }

    private Object getMapSafety(Map<Object, Object> address, String key) {
        return Optional.ofNullable(address).map(map -> map.get(key)).orElse(null);
    }
}
