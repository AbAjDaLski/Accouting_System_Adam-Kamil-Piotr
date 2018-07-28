package pl.coderstrust.accounting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class InvoiceEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String description;
  private BigDecimal price;
  private Vat vat;

  public InvoiceEntry() {
  }

  @JsonCreator
  public InvoiceEntry(@JsonProperty("description") String description,
      @JsonProperty("price") BigDecimal price, @JsonProperty("vat") Vat vat) {
    this.description = description;
    this.price = price;
    this.vat = vat;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Vat getVat() {
    return vat;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    InvoiceEntry that = (InvoiceEntry) obj;
    return Objects.equals(description, that.description)
        && Objects.equals(price, that.price)
        && vat == that.vat;
  }
}