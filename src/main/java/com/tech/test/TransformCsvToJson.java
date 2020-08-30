package com.tech.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TransformCsvToJson {

  public static File folder = new File("src/main/resources/input");
  /*
   * public static void main(String[] args) throws IOException { readCsvFile(); }
   */

  @Scheduled(cron = "0 0/5 * * * ?")
  public void readCsvFile() throws IOException {

    for (final File fileEntry : folder.listFiles()) {
      CSVReader reader = new CSVReader(new FileReader(fileEntry), ',', '\'', 1);
      
      ColumnPositionMappingStrategy<Items> beanStrategy = new ColumnPositionMappingStrategy<Items>();
      beanStrategy.setType(Items.class);
      beanStrategy.setColumnMapping(new String[] {"baseUrl", "level1Name", "level1Id", "level1Url",
          "level2Name", "level2Id", "level2Url", "level3Name", "level3Id", "level3Url"});
      CsvToBean<Items> csvToBean = new CsvToBean<Items>();

      List<Items> items = csvToBean.parse(beanStrategy, reader);

      List<Items> level1 =
          items.stream().filter(distinctByKey(Items::getLevel1Name)).collect(Collectors.toList());

      List<Items> level2 =
          items.stream().filter(distinctByKey(Items::getLevel2Name)).collect(Collectors.toList());

      createItemTree(items, level1, level2, fileEntry.getName());

      log.info("items.size: {}", items.size());
      log.info("items: {}", items);
    }
  }

  public static void createItemTree(List<Items> items, List<Items> level1, List<Items> level2, String fileName) throws IOException {
    List<ItemTree> itemTree = new ArrayList<ItemTree>();
    for (Items itemLevel1 : level1) {
      if (!itemLevel1.getLevel1Name().isBlank() && !itemLevel1.getLevel1Name().isEmpty()) {
        ItemTree tree = new ItemTree();
        tree.setLabel(itemLevel1.getLevel1Name());
        tree.setId(itemLevel1.getLevel1Id());
        tree.setLink(itemLevel1.getBaseUrl());
        List<ItemTree> childrens = new ArrayList<ItemTree>();
        for (Items itemLevel2 : level2) {
          if (!itemLevel2.getLevel2Name().isBlank() && !itemLevel2.getLevel2Name().isEmpty()) {
            ItemTree tree1 = new ItemTree();
            tree1.setLabel(itemLevel2.getLevel2Name());
            tree1.setId(itemLevel2.getLevel2Id());
            tree1.setLink(itemLevel2.getLevel2url());
            tree1.setChildren(getChildrensL3(items, itemLevel2.getLevel2Name()));
            childrens.add(tree1);
          }
          tree.setChildren(childrens);
        }
        itemTree.add(tree);
      }
    }
    log.info("itemTree: {}", itemTree);
    String json = new Gson().toJson(itemTree);
    log.info("json: {}", json);
    Files.write(Paths.get("src/main/resources/output/"+fileName.replaceAll(".csv", ".json")), json.getBytes());
  }

  public static List<ItemTree> getChildrensL3(List<Items> items, String level2Name) {
    List<ItemTree> childrens = new ArrayList<ItemTree>();
    for (Items itemLevel3 : items) {
      if (!itemLevel3.getLevel2Name().isBlank() && !itemLevel3.getLevel2Name().isEmpty()
          && !itemLevel3.getLevel3Name().isBlank() && !itemLevel3.getLevel3Name().isEmpty()
          && itemLevel3.getLevel2Name().equals(level2Name)) {
        ItemTree tree1 = new ItemTree();
        tree1.setLabel(itemLevel3.getLevel3Name());
        tree1.setId(itemLevel3.getLevel3Id());
        tree1.setLink(itemLevel3.getLevel3Url());
        tree1.setChildren(new ArrayList<ItemTree>());
        childrens.add(tree1);
      }
    }
    return childrens;
  }

  public static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }
}
