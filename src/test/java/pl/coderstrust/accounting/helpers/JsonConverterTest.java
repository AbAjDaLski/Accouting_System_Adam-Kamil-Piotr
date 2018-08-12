package pl.coderstrust.accounting.helpers;

import static junitparams.JUnitParamsRunner.$;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.coderstrust.accounting.model.Invoice;

import java.io.IOException;

@RunWith(JUnitParamsRunner.class)
public class JsonConverterTest {

  Object[] parameters = $(
      $("{\"id\":5,\"identifier\":\"TestIdentifier5\",\"issuedDate\":\"2018-01-01\","
              + "\"buyer\":{\"name\":\"CompanyBuyerTest5\",\"taxId\":\"5555555555\","
              + "\"streetAndNumber\":\"Test Buyer Street 5\",\"postalCode\":\"55-555\","
              + "\"location\":\"TestLocationBuyer4\"},\"seller\":{\"name\":\"CompanySellerTest5\","
              + "\"taxId\":\"5555555555\",\"streetAndNumber\":\"Test Seller Street 5\","
              + "\"postalCode\":\"55-555\",\"location\":\"TestLocationSeller4\"},\"entries\":[]}",
          InvoiceHelper.getSampleInvoiceWithId5()),
      $("{\"id\":6,\"identifier\":\"TestIdentifier6\",\"issuedDate\":\"2018-01-01\","
              + "\"buyer\":{\"name\":\"CompanyBuyerTest6\",\"taxId\":\"666666666\","
              + "\"streetAndNumber\":\"Test Buyer Street 6\",\"postalCode\":\"66666\","
              + "\"location\":\"TestLocationBuyer4\"},\"seller\":{\"name\":\"CompanySellerTest6\","
              + "\"taxId\":\"666666666\",\"streetAndNumber\":\"Test Seller Street 4\","
              + "\"postalCode\":\"66666\",\"location\":\"TestLocationSeller4\"},"
              + "\"entries\":[{\"description\":\"Test Entry #4\",\"price\":10,\"vat\":8}]}",
          InvoiceHelper.getSampleInvoiceWithId6()),
      $("{\"id\":7,\"identifier\":\"TestIdentifier7\",\"issuedDate\":\"2018-01-01\","
              + "\"buyer\":{\"name\":\"CompanyBuyerTest7\",\"taxId\":\"777777777\","
              + "\"streetAndNumber\":\"Test Buyer Street 7\",\"postalCode\":\"77777\","
              + "\"location\":\"TestLocationBuyer4\"},\"seller\":{\"name\":\"CompanySellerTest7\","
              + "\"taxId\":\"777777777\",\"streetAndNumber\":\"Test Seller Street 7\","
              + "\"postalCode\":\"77777\",\"location\":\"TestLocationSeller4\"},"
              + "\"entries\":[{\"description\":\"Test Entry #1\",\"price\":10,\"vat\":23},"
              + "{\"description\":\"Test Entry #2\",\"price\":10,\"vat\":8},"
              + "{\"description\":\"Test Entry #3\",\"price\":10,\"vat\":5},"
              + "{\"description\":\"Test Entry #4\",\"price\":10,\"vat\":0}]}",
          InvoiceHelper.getSampleInvoiceWithId7())
  );

  @Parameters(method = "ParametersForShouldCheckIfInvoiceConvertedToJson")
  @Test
  public void ShouldCheckIfInvoiceConvertedToJson(String expected, Invoice given)
      throws IOException {

    String stringToJson = JsonConverter.toJson(given);
    Assert.assertThat(expected, is(equalTo(stringToJson)));
  }

  private Object[] ParametersForShouldCheckIfInvoiceConvertedToJson() {

    return parameters;
  }

  @Parameters(method = "ParametersForShouldCheckIfInvoiceConvertedFromJson")
  @Test
  public void ShouldCheckIfInvoiceConvertedFromJson(String json, Invoice expected)
      throws IOException {

    Invoice invoiceFromJson = JsonConverter.fromJson(json);

    invoicesAreIdentical(expected, invoiceFromJson);
  }

  private Object[] ParametersForShouldCheckIfInvoiceConvertedFromJson() {

    return parameters;
  }

  @Parameters(method = "ParametersForShouldCheckIfInvoiceConvertedToAndFromJson")
  @Test
  public void ShouldCheckIfInvoiceConvertedToAndFromJson(Invoice expected)
      throws IOException {

    String toJson = JsonConverter.toJson(expected);
    Invoice invoiceFromJson = JsonConverter.fromJson(toJson);

    invoicesAreIdentical(expected, invoiceFromJson);
  }

  private Object[] ParametersForShouldCheckIfInvoiceConvertedToAndFromJson() throws IOException {
    return $(
        $(InvoiceHelper.getSampleInvoiceWithId5()),
        $(InvoiceHelper.getSampleInvoiceWithId6()),
        $(InvoiceHelper.getSampleInvoiceWithId7())
    );
  }

  public static void invoicesAreIdentical(Invoice invoice, Invoice invoiceToCompare) {
    assertThat(invoice.getId(), is(invoiceToCompare.getId()));
    assertThat(invoice.getIdentifier(), is(invoiceToCompare.getIdentifier()));
    assertThat(invoice.getIssuedDate(), is(invoiceToCompare.getIssuedDate()));
    assertThat(invoice.getBuyer(), is(invoiceToCompare.getBuyer()));
    assertThat(invoice.getSeller(), is(invoiceToCompare.getSeller()));
    assertTrue(invoice.getEntries().equals(invoiceToCompare.getEntries()));

  }
}