package com.sybit.airtableandroid;

import static com.sybit.airtableandroid.common.Helper.checkEntityValues;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import com.sybit.airtableandroid.common.Entity;
import com.sybit.airtableandroid.common.Helper;
import com.sybit.airtableandroid.exception.AirtableException;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.io.IOException;
import java.util.Date;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by Carlos Lloret
 */

@Config(constants = BuildConfig.class, sdk = VERSION_CODES.M, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MockTableTest {

  private static final String API_KEY = "api_key";
  private static final String BASE = "base";
  private Table<Entity> entityTable;
  private MockWebServer server = new MockWebServer();

  @Before
  public void setUp() throws Exception {

    server.start();
    String serviceEndpoint = server.url("v0").toString();

    Context appContext = RuntimeEnvironment.application;

    Configuration configuration = new Configuration(API_KEY, serviceEndpoint);
    Airtable airtable = new Airtable(appContext).configure(configuration);
    Base base = airtable.base(BASE);

    entityTable = base.table("Entity", Entity.class);
  }

  @After
  public void tearDown() throws Exception {

    server.shutdown();
  }

  @Test
  public void select_Always_ReturnAllValues() throws Exception {

    String fileName = "entities_all.json";
    enqueueMockResponse(200, fileName);

    TestObserver<Entity> testObserver = entityTable.select()
        .toObservable()
        .concatMap(Observable::fromIterable)
        .test();

    testObserver.awaitTerminalEvent();

    testObserver
        .assertComplete()
        .assertNoErrors()
        .assertValueCount(2);

    RecordedRequest request = server.takeRequest();
    assertEquals("/v0/base/Entity", request.getPath());
    assertEquals("GET", request.getMethod());
  }

  @Test
  public void createEntity_Always_ReturnCreatedValue() throws Exception {

    String fileName = "entity_created.json";
    enqueueMockResponse(200, fileName);

    Date date = Helper.newDate();

    Entity entity = new Entity("New event", 111.1, true, date);

    TestObserver<Entity> testObserver = entityTable.create(entity)
        .toObservable()
        .test();

    checkEntityValues(testObserver, entity);
  }

  @Test
  public void updateEntity_Always_ReturnEditedValue() throws Exception {

    String fileName = "entity_edited.json";
    enqueueMockResponse(200, fileName);

    Entity entity = new Entity("rec0XqfldqwfXeoaa", "Text modified", 111.1, true,
        Helper.newDate());

    TestObserver<Entity> testObserver = entityTable.update(entity)
        .toObservable()
        .test();

    checkEntityValues(testObserver, entity);
  }

  @Test
  public void deleteEvent_Always_ReturnResult() throws Exception {

    String fileName = "entity_deleted.json";
    enqueueMockResponse(200, fileName);

    TestObserver<Boolean> testObserver = entityTable.destroy("id")
        .toObservable()
        .test();

    testObserver
        .assertComplete()
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue(result -> true);
  }

  @Test
  public void select_WhenNotFound_ThrowException() throws Exception {

    String fileName = "entity_not_found.json";
    enqueueMockResponse(404, fileName);

    TestObserver<Entity> testObserver = entityTable.select()
        .toObservable()
        .concatMap(Observable::fromIterable)
        .test();

    testObserver.awaitTerminalEvent();

    testObserver
        .assertFailureAndMessage(AirtableException.class,
            "Could not find what you are looking for (NOT_FOUND) [Http code 404]");
  }

  private void enqueueMockResponse(int code, String fileName) throws IOException {

    MockResponse mockResponse = new MockResponse();
    String fileContent = Helper.readFromInputStream(fileName);

    mockResponse.setResponseCode(code);
    mockResponse.setBody(fileContent);

    server.enqueue(mockResponse);
  }

}
