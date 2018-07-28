package pl.coderstrust.accounting.database;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.coderstrust.accounting.helpers.InvoiceHelper;
import pl.coderstrust.accounting.helpers.JsonConverter;
import pl.coderstrust.accounting.model.Company;
import pl.coderstrust.accounting.model.Invoice;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class HibernateDatabaseTest {

  @Autowired
  private MockMvc mockMvc;

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  private HttpMessageConverter mappingJackson2HttpMessageConverter;

  private List<Invoice> bookmarkList = new ArrayList<>();

  @Autowired
  void setConverters(HttpMessageConverter<?>[] converters) {

    this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
        .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
        .findAny()
        .orElse(null);

    assertNotNull("the JSON message converter must not be null",
        this.mappingJackson2HttpMessageConverter);
  }

  @Test
  public void shouldFindInvoiceById() throws Exception {

    String postResponse = mockMvc.perform(post("/invoices")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(
            JsonConverter.toJson(new Invoice(0, "TestIdentifier0", LocalDate.now(),
                new Company("CompanyBuyerTest1", "1111111111", "Test Buyer Street 1", "11-111",
                    "TestLocationBuyer1"),
                new Company("CompanySellerTest1", "1111111111", "Test Seller Street 1",
                    "11-111",
                    "TestLocationSeller1"), InvoiceHelper.getSampleFourInvoiceEntriesList()))))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    mockMvc.perform(get("/invoices/1"))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldFindInvoicesByDateRange() throws Exception {

    String postResponse = mockMvc.perform(post("/invoices")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(
            JsonConverter.toJson(new Invoice(0, "TestIdentifier0", LocalDate.now(),
                new Company("CompanyBuyerTest1", "1111111111", "Test Buyer Street 1", "11-111",
                    "TestLocationBuyer1"),
                new Company("CompanySellerTest1", "1111111111", "Test Seller Street 1",
                    "11-111",
                    "TestLocationSeller1"), InvoiceHelper.getSampleFourInvoiceEntriesList()))))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    mockMvc.perform(
        get("/invoices/" + LocalDate.now().toString() + "/" + LocalDate.now().plusDays(3)
            .toString()))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldDeleteInvoiceById() throws Exception {

    String postResponse = mockMvc.perform(post("/invoices")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(
            JsonConverter.toJson(new Invoice(0, "TestIdentifier0", LocalDate.now(),
                new Company("CompanyBuyerTest1", "1111111111", "Test Buyer Street 1", "11-111",
                    "TestLocationBuyer1"),
                new Company("CompanySellerTest1", "1111111111", "Test Seller Street 1",
                    "11-111",
                    "TestLocationSeller1"), InvoiceHelper.getSampleFourInvoiceEntriesList()))))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    mockMvc.perform(delete("/invoices")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content("1"))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnEmptyArrayWhenNothingWasAdded() throws Exception {

    mockMvc.perform(get("/invoices"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @RequestMapping(value = "/invoices", method = RequestMethod.POST, headers = "Accept=application/json")
  public void shouldReturnInvoicingWhichWasEarlierAdded() throws Exception {

    String postResponse = mockMvc.perform(post("/invoices")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(
            JsonConverter.toJson(new Invoice(0, "TestIdentifier0", LocalDate.now(),
                new Company("CompanyBuyerTest1", "1111111111", "Test Buyer Street 1", "11-111",
                    "TestLocationBuyer1"),
                new Company("CompanySellerTest1", "1111111111", "Test Seller Street 1",
                    "11-111",
                    "TestLocationSeller1"), InvoiceHelper.getSampleFourInvoiceEntriesList()))))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    mockMvc.perform(get("/invoices"))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(Integer.valueOf(postResponse))))
        .andExpect(jsonPath("$[0].issuedDate", is(LocalDate.now().toString())));
  }
}