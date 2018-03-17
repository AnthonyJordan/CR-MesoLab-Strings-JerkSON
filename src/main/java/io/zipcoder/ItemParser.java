package io.zipcoder;

import java.util.*;
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

    public String printFormattedItemList(ArrayList<Item> itemList){
        ArrayList<ArrayList<Item>> listOfItemLists = seperateItemListIntoListsOfItemTypes(itemList);
        StringBuilder outputString = new StringBuilder();
        for (ArrayList<Item> listOfItems: listOfItemLists) {
            outputString.append(printNameOfItem(listOfItems));
            HashMap<Double, Integer> prices = countPriceOccurancies(listOfItems);
            outputString.append(printPriceOfItem(prices));
        }
        outputString.append(printErrors());
        return outputString.toString();
    }

    private ArrayList<ArrayList<Item>> seperateItemListIntoListsOfItemTypes(ArrayList<Item> itemList) {
        ArrayList<Item> milkList = createItemTypeList(itemList, "Milk");
        ArrayList<Item> breadList = createItemTypeList(itemList, "Bread");
        ArrayList<Item> cookieList = createItemTypeList(itemList, "Cookies");
        ArrayList<Item> appleList = createItemTypeList(itemList, "Apples");
        ArrayList<ArrayList<Item>> listOfItemLists = new ArrayList<ArrayList<Item>>();
        listOfItemLists.add(milkList);
        listOfItemLists.add(breadList);
        listOfItemLists.add(cookieList);
        listOfItemLists.add(appleList);
        return listOfItemLists;
    }

    private ArrayList<Item> createItemTypeList(ArrayList<Item> itemList, String itemName) {
        ArrayList<Item> itemTypeList = new ArrayList<Item>();
        for (Item item: itemList) {
            if (item.getName().equals(itemName)){
                itemTypeList.add(item);
            }
        }
        return itemTypeList;
    }

    public HashMap<Double, Integer> countPriceOccurancies(ArrayList<Item> listOfItems){
        HashMap<Double, Integer> priceCountMap = new HashMap<Double, Integer>();
        for (Item item: listOfItems) {
            if (priceCountMap.containsKey(item.getPrice())) {
                priceCountMap.put(item.getPrice(),priceCountMap.get(item.getPrice()) + 1);
            }else if (!priceCountMap.containsKey(item.getPrice())){
                priceCountMap.put(item.getPrice(), 1);
            }
        }
        return priceCountMap;
    }

    public String printNameOfItem(ArrayList<Item> inputArray){
        StringBuilder outputString = new StringBuilder();
        outputString.append(String.format("name:%8s", inputArray.get(0).getName()));
        outputString.append(printOutputSpace());
        outputString.append(printHowManyTimesSeen(inputArray.size()));
        outputString.append("=============");
        outputString.append(printOutputSpace());
        outputString.append("=============\n");
        return outputString.toString();
    }

    public String printPriceOfItem(HashMap<Double, Integer> mapOfPrices){
        StringBuilder outputString = new StringBuilder();
        for (Map.Entry<Double, Integer> entry: mapOfPrices.entrySet()) {
            outputString.append(String.format("Price:%7s", entry.getKey()));
            outputString.append(printOutputSpace());
            outputString.append(printHowManyTimesSeen(entry.getValue()));
            outputString.append("-------------");
            outputString.append(printOutputSpace());
            outputString.append("-------------\n");
        }
        outputString.append("\n");
        return outputString.toString();
    }

    private String printErrors(){
        StringBuilder outputString = new StringBuilder("Errors       ");
        outputString.append(printOutputSpace());
        outputString.append(printHowManyTimesSeen(this.exceptionCount));
        return outputString.toString();
    }

    private String printHowManyTimesSeen(int howManyTimesSeen) {
        if (howManyTimesSeen == 1){
            return String.format("seen:%2d time \n", howManyTimesSeen);
        } else {
            return String.format("seen:%2d times\n", howManyTimesSeen);
        }
    }

    private String printOutputSpace() {
        return "       ";
    }

}
