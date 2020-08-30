package com.tech.test;

import java.io.IOException;
import org.junit.Test;

public class TransformCsvToJsonTest {

  private TransformCsvToJson getSut() throws Exception {
    return new TransformCsvToJson();
  }
  
  @Test
  public void testReadCsvFile() throws IOException, Exception {
    
    getSut().readCsvFile();
  }
}
