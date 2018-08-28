package pl.coderstrust.accounting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.math.BigDecimal;
import java.util.Arrays;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public enum Vat {
  REGULAR(BigDecimal.valueOf(23)), REDUCED1(BigDecimal.valueOf(8)), REDUCED2(BigDecimal.valueOf(5)), ZERO(BigDecimal.valueOf(0));

  Vat(BigDecimal rate) {
    this.rate = rate;
  }

  private final BigDecimal rate;
  @Id
  @GeneratedValue
 // do poprawy BigDecimal
  private final int rate;

  @JsonCreator
  public static Vat fromValue(BigDecimal value) {
    return Arrays.stream(Vat.values())
        .filter(status -> status.getValue().equals(value))
        .findFirst()
        .orElse(null);
  }

  @JsonValue
  public BigDecimal getValue() {
    return rate;
  }
}