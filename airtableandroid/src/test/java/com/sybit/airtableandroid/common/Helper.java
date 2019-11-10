package com.sybit.airtableandroid.common;

import static com.google.common.truth.Truth.assertThat;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import org.joda.time.LocalDate;

/**
 * Created by Carlos Lloret
 */

public class Helper {

  public static <T> Predicate<T> check(Consumer<T> consumer) {

    return t -> {
      consumer.accept(t);
      return true;
    };
  }

  public static void checkEntityValues(TestObserver<Entity> testObserver, Entity newEntity) {

    testObserver
        .assertComplete()
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue(check(
            result -> assertThat(result.getText())
                .isEqualTo(newEntity.getText())
        ))
        .assertValue(check(
            result -> assertThat(result.getNumber())
                .isEqualTo(newEntity.getNumber())
        ))
        .assertValue(check(
            result -> assertThat(result.getDate())
                .isEqualTo(newEntity.getDate())
        ))
        .assertValue(check(
            result -> assertThat(result.isCheckbox())
                .isEqualTo(newEntity.isCheckbox())
        ))
        .assertValue(check(
            result -> assertThat(result.getNullableInteger())
                .isEqualTo(newEntity.getNullableInteger())
        ));
  }

  public static Date newDate() {

    return new LocalDate()
        .withYear(2018)
        .withMonthOfYear(3)
        .withDayOfMonth(31)
        .toDate();
  }

  public static String readFromInputStream(String fileName)
      throws IOException {

    InputStream inputStream = Helper.class.getClassLoader().getResourceAsStream(fileName);
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
      }
    }
    return resultStringBuilder.toString();
  }
}
