package com.tech.test;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemTree {

  private String label;
  private String id;
  private String link;
  private List<ItemTree> children;
}
