package com.sybit.airtableandroid;

import static com.google.common.truth.Truth.assertThat;
import static com.sybit.airtableandroid.common.Helper.check;
import static com.sybit.airtableandroid.common.Helper.checkEntityValues;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import com.sybit.airtableandroid.common.Entity;
import com.sybit.airtableandroid.common.Helper;
import com.sybit.airtableandroid.exception.AirtableException;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.net.HttpURLConnection;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Carlos Lloret
 */

@Config(sdk = VERSION_CODES.M, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TableTest {

  private static final String API_KEY = BuildConfig.AIRTABLE_API_KEY;
  private static final String BASE = BuildConfig.AIRTABLE_BASE_TEST;
  private static final String UPDATE_RECORD_ID = "recQOHC2KzU9Rn5dR";
  private static final String FIND_RECORD_ID = "rec7KrK506mfubD7N";
  private static final String FIND_RECORD_ID_NOT = "recXXXXXXXXXXXXXX";
  private Table<Entity> entityTable;

  @Before
  public void setUp() throws Exception {

    Context appContext = ApplicationProvider.getApplicationContext();

    Airtable airtable = new Airtable(appContext).configure(API_KEY);
    Base base = airtable.base(BASE);

    entityTable = base.table("Entity", Entity.class);
  }

  @Test
  public void select_Always_ReturnAllValues() {

    TestObserver<Entity> subscriber = new TestObserver<>();

    entityTable.select("Main")
        .toObservable()
        .concatMap(Observable::fromIterable)
        .subscribe(subscriber);

    subscriber
        .assertComplete()
        .assertNoErrors()
        .assertValueAt(0,
            check(result -> assertThat(result.getText()).isEqualTo("Text 1")));
  }

  @Test
  public void find_Always_ReturnCorrectValue() {

    TestObserver<Entity> testObserver = new TestObserver<>();

    entityTable.find(FIND_RECORD_ID)
        .subscribe(testObserver);

    Date date = Helper.newDate();

    Entity newEntity = new Entity(FIND_RECORD_ID, "Text 1", 111.1, true, date, 999);

    checkEntityValues(testObserver, newEntity);
  }

  @Test
  public void find_WhenNotFound_ThrowAirtableException() {

    TestObserver<Entity> testObserver = new TestObserver<>();

    entityTable.find(FIND_RECORD_ID_NOT)
        .subscribe(testObserver);

    testObserver.assertFailure(error -> {
      AirtableException airtableException = (AirtableException) error;

      return airtableException.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND;
    });
  }

  @Test
  public void create_Always_ReturnCreatedValue() {

    TestObserver<Entity> testObserver = new TestObserver<>();

    Date date = Helper.newDate();

    Entity newEntity = new Entity("Text 3", 333.3, true, date, 999);

    entityTable.create(newEntity)
        .subscribe(testObserver);

    checkEntityValues(testObserver, newEntity);
  }

  @Test
  public void create_WhenNullInteger_CreateCorrectValues() {

    TestObserver<Entity> testObserver = new TestObserver<>();

    Date date = Helper.newDate();

    Entity newEntity = new Entity("Text 3", 333.3, true, date, null);

    entityTable.create(newEntity)
        .subscribe(testObserver);

    checkEntityValues(testObserver, newEntity);
  }

  @Test
  public void update_Always_ReturnUpdatedValue() {

    TestObserver<Entity> testObserver = new TestObserver<>();

    Entity newEntity = new Entity(UPDATE_RECORD_ID, "Text modified", 111.1, true,
        Helper.newDate(), 999);

    entityTable.update(newEntity)
        .subscribe(testObserver);

    checkEntityValues(testObserver, newEntity);
  }

  @Test
  public void update_WhenNullInteger_UpdateCorrectValues() {

    TestObserver<Entity> testObserver = new TestObserver<>();

    Entity newEntity = new Entity(UPDATE_RECORD_ID, "Text modified", 111.1, true,
        Helper.newDate(), null);

    entityTable.update(newEntity)
        .subscribe(testObserver);

    checkEntityValues(testObserver, newEntity);
  }

  @Test
  public void destroy_Always_ReturnTrue() {

    Date date = Helper.newDate();
    Entity newEntity = new Entity("Text 3", 333.3, true, date, 999);

    Maybe<Entity> entityMaybe = entityTable.create(newEntity);
    Entity entity = entityMaybe.blockingGet();

    TestObserver<Boolean> testObserver = entityTable.destroy(entity.getId())
        .toObservable()
        .test();

    testObserver
        .assertComplete()
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue(result -> true);
  }

}