package io.github.ottocline.OpenSearchRAGPlayground;

import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;

public class App {

  public static OpenSearchClient createClient() {
    HttpHost host = new HttpHost("http", "localhost", 9200);

    OpenSearchTransport transport = ApacheHttpClient5TransportBuilder
            .builder(host)
            .setMapper(new JacksonJsonpMapper())
            .build();

    return new OpenSearchClient(transport);
  }

  public static void main(String[] args) {
    OpenSearchTransport transport = null;

    try {
      String index = "sample-index";

      // connection
      HttpHost host = new HttpHost("http", "localhost", 9200);
      transport = ApacheHttpClient5TransportBuilder
              .builder(host)
              .setMapper(new JacksonJsonpMapper())
              .build();
      OpenSearchClient client = new OpenSearchClient(transport);

      // here we just check if index exists
      if (!client.indices().exists(r -> r.index(index)).value()) {
        System.out.println("Creating index: " + index);
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index(index)
                .build();
        client.indices().create(createIndexRequest);
      } else {
        System.out.println("Index already exists: " + index);
      }

      StuffDocument fun = new StuffDocument(
              "Fun Title",
              "Very fun document we have here",
              "Strongly Typed"
      );
      StuffDocument notfun = new StuffDocument(
              "Angry Title",
              "Not so fun...",
              "WEAKLY Typed"
      );


      System.out.println("Indexing documents...");
      client.index(i -> i
              .index(index)
              .id("1")
              .document(fun));

      client.index(i -> i
              .index(index)
              .id("2")
              .document(notfun));

      // gotta wait for the indexing to happen asynchronously
      Thread.sleep(1000);

      System.out.println("\nLooking for those docs of yours..");
      SearchResponse<StuffDocument> response = client.search(
              s -> s.index(index).query(q -> q.matchAll(m -> m)),
              StuffDocument.class
      );

      System.out.println("Found " + response.hits().total().value() + " documents:");
      for (var hit : response.hits().hits()) {
        StuffDocument foundDoc = hit.source();
        System.out.println("- " + foundDoc.getTitle() + " (category: " + foundDoc.getCategory() + ")");
      }

      //deleted the indexes afterwards for the sake of the example
      //note to self: dont need to do this if building real system
      System.out.println("\nDeleting index: " + index);
      client.indices().delete(d -> d.index(index));

      System.out.println("Dunzo, index deleted");

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();
    } finally {

      //close transport
      if (transport != null) {
        try {
          transport.close();
        } catch (Exception e) {
          System.err.println("Error closing transport: " + e.getMessage());
        }
      }
    }
  }
}