package io.zipcoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemParser {

    private int exceptionCount = 0;

    public ArrayList<String> parseRawDataIntoStringArray(String rawData) {
        String stringPattern = "##";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern, rawData);
        return response;
    }

    public Item parseStringIntoItem(ArrayList<String> rawItem) throws ItemParseException {
        try {
            HashMap<String, String> keyValuePairMap = stringToHashMap(rawItem);
            String name = keyValuePairMap.get("name");
            Double price = Double.parseDouble(keyValuePairMap.get("price"));
            String type = keyValuePairMap.get("type");
            String expiration = keyValuePairMap.get("expiration");
            return new Item(name, price, type, expiration);
        } catch (IndexOutOfBoundsException e) {
            throw new ItemParseException();
        }
    }

    public ArrayList<String> findKeyValuePairsInRawItemData(String rawItem) {
        String stringPattern = "[;|^|%|*|!|@]";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern, rawItem);
        return response;
    }

    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString) {
        return new ArrayList<String>(Arrays.asList(inputString.split(stringPattern)));
    }

    public ArrayList<ArrayList<String>> splitItemsAndPairs(String stringOfItems) {
        ArrayList<String> rawitemArray = parseRawDataIntoStringArray(stringOfItems);
        ArrayList<ArrayList<String>> itemsAndPairsSplitArray = new ArrayList<ArrayList<String>>();
        for (String string : rawitemArray) {
            itemsAndPairsSplitArray.add(findKeyValuePairsInRawItemData(string));
        }
        return itemsAndPairsSplitArray;
    }

    private String regexChanger(String stringToChange, String stringToBecome, String stringToSearchThrough) {
        Pattern pattern = Pattern.compile(stringToChange);
        Matcher matcher = pattern.matcher(stringToSearchThrough);
        return matcher.replaceAll(stringToBecome);
    }

    public String cleanUpString(String stringToClean) {
        stringToClean = regexChanger("(?i)bread", "Bread", stringToClean);
        stringToClean = regexChanger("(?i)milk", "Milk", stringToClean);
        stringToClean = regexChanger("(?i)apples", "Apples", stringToClean);
        stringToClean = regexChanger("(?i)c..kies", "Cookies", stringToClean);
        stringToClean = regexChanger("(?i)name", "name", stringToClean);
        stringToClean = regexChanger("(?i)price", "price", stringToClean);
        stringToClean = regexChanger("(?i)type", "type", stringToClean);
        stringToClean = regexChanger("(?i)expiration", "expiration", stringToClean);
        return stringToClean;
    }

    public HashMap<String, String> stringToHashMap(ArrayList<String> inputArrayList) throws IndexOutOfBoundsException {
        HashMap<String, String> keyValuePairMap = new HashMap<String, String>();
        for (String string : inputArrayList) {
            System.out.println(string);
            ArrayList<String> keyValuePairArray = splitStringWithRegexPattern(":", string);
            keyValuePairMap.put(keyValuePairArray.get(0), keyValuePairArray.get(1));
        }
        return keyValuePairMap;
    }

    public ArrayList<Item> turnStringIntoItems(ArrayList<ArrayList<String>> inputArrayArray) {
        ArrayList<Item> itemArrayList = new ArrayList<Item>();
        for (ArrayList<String> inputArray : inputArrayArray) {
            try {
                itemArrayList.add(parseStringIntoItem(inputArray));
            } catch (ItemParseException e) {
                this.exceptionCount++;
            }
        }
        return itemArrayList;
    }

}
