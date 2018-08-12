package pl.coderstrust.accounting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public enum Vat {
  REGULAR(23), REDUCED1(8), REDUCED2(5), ZERO(0);

  Vat(int rate) {
    this.rate = rate;
  }

  @Id
  @GeneratedValue
 // do poprawy BigDecimal
  private final int rate;

  @JsonCreator
  public static Vat fromValue(int value) {
    return Arrays.stream(Vat.values())
        .filter(status -> status.getValue() == value)
        .findFirst()
        .orElse(null);
  }

  @JsonValue
  public int getValue() {
    return rate;
  }
}