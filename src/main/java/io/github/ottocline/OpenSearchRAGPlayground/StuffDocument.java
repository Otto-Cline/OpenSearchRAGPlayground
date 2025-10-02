package io.github.ottocline.OpenSearchRAGPlayground;

public class StuffDocument {
  private String title;
  private String content;
  private String category;

  // need default constructor for opensearch deserialization
  public StuffDocument() {}

  public StuffDocument(String title, String content, String category) {
    this.title = title;
    this.content = content;
    this.category = category;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public String toString() {
    return "TradingDocument{" +
            "title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", category='" + category + '\'' +
            '}';
  }
}
