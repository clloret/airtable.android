package com.sybit.airtableandroid;

import static com.sybit.airtableandroid.common.Helper.check;
import static com.sybit.airtableandroid.common.Helper.checkEntityValues;
import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import com.sybit.airtableandroid.common.Entity;
import com.sybit.airtableandroid.common.Helper;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.util.Date;
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
public class TableTest {

  private static final String API_KEY = BuildConfig.AIRTABLE_API_KEY;
  private static final String BASE = BuildConfig.AIRTABLE_BASE_TEST;
  private static final String UPDATE_RECORD_ID = "recQOHC2KzU9Rn5dR";
  private static final String FIND_RECORD_ID = "rec7KrK506mfubD7N";
  private Table<Entity> entityTable;

  @Before
  public void setUp() throws Exception {

    Context appContext = RuntimeEnvironment.application;

    Airtable airtable = new Airtable(appContext).configure(API_KEY);
    Base base = airtable.base(BASE);

    entityTable = base.table("Entity", Entity.class);
  }

  @Test
  public void select() {

    TestObserver<Entity> subscriber = new TestObserver<>();

    entityTable.select("Main")
        .toObservable()
        .concatMap(Observable::fromIterable)
        .subscribe(subscriber);

    subscriber
        .assertComplete()
        .assertNoErrors()
        .assertValueAt(0, check(result -> assertThat(result.getText()).isEqualTo("Text 1")));
  }

  @Test
  public void find() {

    TestObserver<Entity> testObserver = new TestObserver<>();

    entityTable.find(FIND_RECORD_ID)
        .subscribe(testObserver);

    Date date = Helper.newDate();

    Entity newEntity = new Entity(FIND_RECORD_ID, "Text 1", 111.1, true, date);

    checkEntityValues(testObserver, newEntity);
  }

  @Test
  public void create() {

    TestObserver<Entity> testObserver = new TestObserver<>();

    Date date = Helper.newDate();

    Entity newEntity = new Entity("Text 3", 333.3, true, date);

    entityTable.create(newEntity)
        .subscribe(testObserver);

    checkEntityValues(testObserver, newEntity);
  }

  @Test
  public void update() {

    TestObserver<Entity> testObserver = new TestObserver<>();

    Entity newEntity = new Entity(UPDATE_RECORD_ID, "Text modified", 111.1, true,
        Helper.newDate());

    entityTable.update(newEntity)
        .subscribe(testObserver);

    checkEntityValues(testObserver, newEntity);
  }

  @Test
  public void destroy() {

    Date date = Helper.newDate();
    Entity newEntity = new Entity("Text 3", 333.3, true, date);

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