package com.tech.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Items {

  private String baseUrl;
  private String level1Name;
  private String level1Id;
  private String level1Url;
  private String level2Name;
  private String level2Id;
  private String level2url;
  private String level3Name;
  private String level3Id;
  private String level3Url;
}
