package io.zipcoder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class ItemParserTest {

    private String rawSingleItem =    "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##";

    private String rawSingleItemIrregularSeperatorSample = "naMe:MiLK;price:3.23;type:Food^expiration:1/11/2016##";

    private String rawBrokenSingleItem =    "naMe:;price:3.23;type:Food;expiration:1/25/2016##";

    private String rawMultipleItems = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##"
                                      +"naME:BreaD;price:1.23;type:Food;expiration:1/02/2016##"
                                      +"NAMe:BrEAD;price:1.23;type:Food;expiration:2/25/2016##";
    private ItemParser itemParser;

    @Before
    public void setUp(){
        itemParser = new ItemParser();
    }

    @Test
    public void parseRawDataIntoStringArrayTest(){
        Integer expectedArraySize = 3;
        ArrayList<String> items = itemParser.parseRawDataIntoStringArray(rawMultipleItems);
        Integer actualArraySize = items.size();
        assertEquals(expectedArraySize, actualArraySize);
    }

    @Test
    public void parseStringIntoItemTest() throws ItemParseException{
        Item expected = new Item("Milk", 3.23, "Food","1/25/2016");
        Item actual = itemParser.parseStringIntoItem(itemParser
                .splitItemsAndPairs(itemParser.cleanUpString(rawSingleItem)).get(0));
        assertEquals(expected.toString(), actual.toString());
    }

    @Test(expected = ItemParseException.class)
    public void parseBrokenStringIntoItemTest() throws ItemParseException{
        itemParser.parseStringIntoItem(itemParser.splitItemsAndPairs(rawBrokenSingleItem).get(0));
    }

    @Test
    public void findKeyValuePairsInRawItemDataTest(){
        Integer expected = 4;
        Integer actual = itemParser.findKeyValuePairsInRawItemData(rawSingleItem).size();
        assertEquals(expected, actual);
    }

    @Test
    public void findKeyValuePairsInRawItemDataTestIrregular(){
        Integer expected = 4;
        Integer actual = itemParser.findKeyValuePairsInRawItemData(rawSingleItemIrregularSeperatorSample).size();
        assertEquals(expected, actual);
    }

    @Test
    public void splitItemsAndPairsTest(){
        ArrayList<ArrayList<String>> testList = itemParser.splitItemsAndPairs(rawMultipleItems);
        String expected = "naMe:Milk";
        String actual = testList.get(0).get(0);
        Assert.assertEquals(actual,expected);
    }

    @Test
    public void cleanUpStringTest(){
        String expectec = "Milk";
        String actual = itemParser.cleanUpString("MILK");
        Assert.assertEquals(expectec,actual);
    }

    @Test
    public void countPriceOccurancesTest(){
        Item testItem1 = new Item("Milk", 3.23, "Food", "3/25/2018");
        Item testItem2 = new Item("Milk", 3.54, "Food", "3/25/2018");
        ArrayList<Item> testArrayList = new ArrayList<Item>();
        testArrayList.add(testItem1);
        testArrayList.add(testItem2);
        HashMap<Double, Integer> actualMap = itemParser.countPriceOccurancies(testArrayList);
        HashMap<Double, Integer> expectedMap = new HashMap<Double, Integer>();
        expectedMap.put(3.23, 1);
        expectedMap.put(3.54, 1);
        Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void printNameOfItemTest(){
        Item testItem1 = new Item("Milk", 3.23, "Food", "3/25/2018");
        ArrayList<Item> testArrayList = new ArrayList<Item>();
        testArrayList.add(testItem1);
        String actual = itemParser.printNameOfItem(testArrayList);
        String expected = "name:    Milk       seen: 1 time \n" +
                "=============       =============\n";
        Assert.assertEquals(actual,expected);
    }

    @Test
    public void printPriceOfItemTest(){
        Item testItem1 = new Item("Milk", 3.23, "Food", "3/25/2018");
        Item testItem2 = new Item("Milk", 3.54, "Food", "3/25/2018");
        ArrayList<Item> testArrayList = new ArrayList<Item>();
        testArrayList.add(testItem1);
        testArrayList.add(testItem2);
        String actual = itemParser.printPriceOfItem(itemParser.countPriceOccurancies(testArrayList));
        String expected = "Price:   3.23       seen: 1 time \n" +
                "-------------       -------------\n" +
                "Price:   3.54       seen: 1 time \n" +
                "-------------       -------------\n";
        Assert.assertEquals(actual,expected);
    }

    @Test
    public void printFormattedItemListTest(){
        Item testItem1 = new Item("Cookies", 3.23, "Food", "3/25/2018");
        Item testItem2 = new Item("Milk", 3.54, "Food", "3/25/2018");
        Item testItem3 = new Item("Apples", 3.54, "Food", "3/25/2018");
        Item testItem4 = new Item("Bread", 3.54, "Food", "3/25/2018");
        ArrayList<Item> testArrayList = new ArrayList<Item>();
        testArrayList.add(testItem1);
        testArrayList.add(testItem2);
        testArrayList.add(testItem3);
        testArrayList.add(testItem4);
        String actual = itemParser.printFormattedItemList(testArrayList);
        String expected = "name:    Milk       seen: 1 time \n" +
                "=============       =============\n" +
                "Price:   3.54       seen: 1 time \n" +
                "-------------       -------------\n" +
                "\n" +
                "name:   Bread       seen: 1 time \n" +
                "=============       =============\n" +
                "Price:   3.54       seen: 1 time \n" +
                "-------------       -------------\n" +
                "\n" +
                "name: Cookies       seen: 1 time \n" +
                "=============       =============\n" +
                "Price:   3.23       seen: 1 time \n" +
                "-------------       -------------\n" +
                "\n" +
                "name:  Apples       seen: 1 time \n" +
                "=============       =============\n" +
                "Price:   3.54       seen: 1 time \n" +
                "-------------       -------------\n" +
                "\n" +
                "Errors              seen: 0 times\n";
        Assert.assertEquals(actual,expected);
    }

}
