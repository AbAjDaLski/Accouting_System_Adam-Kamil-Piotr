package pl.coderstrust.accounting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Invoice {

  @ApiModelProperty(value = "id invoice", example = "1")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @ApiModelProperty(value = "Invoice of number FV/RRRR/MM/DD", example = "FV/2018/05/23/1")
  private String identifier;
  @ApiModelProperty(value = "Format date RRRR-MM-DD")
  private LocalDate issuedDate;

  @ManyToOne(cascade = CascadeType.ALL)
  private Company buyer;
  @ManyToOne(cascade = CascadeType.ALL)
  private Company seller;
  @OneToMany(cascade = CascadeType.ALL)
  private List<InvoiceEntry> entries;

  public Invoice() {
  }

  @JsonCreator
  public Invoice(@JsonProperty("id") Integer id, @JsonProperty("identifier") String identifier,
      @JsonProperty("issuedDate") LocalDate issuedDate, @JsonProperty("buyer") Company buyer,
      @JsonProperty("seller") Company seller, @JsonProperty("entries") List<InvoiceEntry> entries) {
    this.id = id;
    this.identifier = identifier;
    this.issuedDate = issuedDate;
    this.buyer = buyer;
    this.seller = seller;
    this.entries = entries;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Invoice invoice = (Invoice) obj;
    return Objects.equals(id, invoice.id)
        && Objects.equals(identifier, invoice.identifier)
        && Objects.equals(issuedDate, invoice.issuedDate)
        && Objects.equals(buyer, invoice.buyer)
        && Objects.equals(seller, invoice.seller)
        && Objects.equals(entries, invoice.entries);
  }

  @Override
  public String toString() {
    return "Invoice{"
        + "id=" + id
        + ", identifier='" + identifier + '\''
        + ", issuedDate=" + issuedDate
        + ", buyer=" + buyer
        + ", seller=" + seller
        + ", entries=" + entries
        + '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, identifier, issuedDate, buyer, seller, entries);
  }

  public Invoice(String identifier, LocalDate issuedDate, Company buyer, Company seller,
      List<InvoiceEntry> entries) {
    this(null, identifier, issuedDate, buyer, seller, entries);
  }

  public Integer getId() {
    return id;
  }

  public String getIdentifier() {
    return identifier;
  }

  public LocalDate getIssuedDate() {
    return issuedDate;
  }

  public Company getBuyer() {
    return buyer;
  }

  public Company getSeller() {
    return seller;
  }

  public List<InvoiceEntry> getEntries() {
    return entries;
  }
}