package pl.coderstrust.accounting;


import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.accounting.helpers.InvoiceHelper;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InvoiceControllerTest {

  @Autowired
  private MockMvc mockMvc;

  String urlInvoices = "/invoices";

  @Test
  public void shouldReturnEmptyArrayWhenNothingWasAdded() throws Exception {
    mockMvc
        .perform(get(urlInvoices))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)))
        .andExpect(content().string(containsString("[]")));
  }

  @Test
  public void shouldReturnInvoiceWhichWasEarlierAdded() throws Exception {
    String postResponse = mockMvc
        .perform(post(urlInvoices)
            .content(InvoiceHelper.simpleInvoiceId5Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    mockMvc
        .perform(get(urlInvoices))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].identifier", is("TestIdentifier5")))
        .andExpect(jsonPath("$[0].id", is(Integer.valueOf(postResponse))))
        .andExpect(content().string(containsString("[]")));
  }

  @Test
  public void shouldReturnTwoInvoicesWhichWereEarlierAdded() throws Exception {
    mockMvc
        .perform(post(urlInvoices)
            .content(InvoiceHelper.simpleInvoiceId5Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(post(urlInvoices)
            .content(InvoiceHelper.simpleInvoiceId6Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(get(urlInvoices))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].identifier", is("TestIdentifier5")))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[1].identifier", is("TestIdentifier6")))
        .andExpect(jsonPath("$[1].id", is(2)));
  }

  @Test
  public void shouldRemoveInvoiceWhichWasEarlierAdded() throws Exception {
    mockMvc
        .perform(post(urlInvoices)
            .content(InvoiceHelper.simpleInvoiceId5Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(delete(urlInvoices)
            .content("1")
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(get(urlInvoices))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  public void shouldReturnSpecificInvoiceWhichWasEarlierAdded() throws Exception {
    mockMvc
        .perform(post(urlInvoices)
            .content(InvoiceHelper.simpleInvoiceId5Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(post(urlInvoices)
            .content(InvoiceHelper.simpleInvoiceId6Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/invoices/2"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].identifier", is("TestIdentifier6")))
        .andExpect(jsonPath("$[0].id", is(2)));
  }

  @Test
  public void shouldReturnTwoInvoicesWithSetDateRangeThatWereEarlierAdded() throws Exception {
    mockMvc
        .perform(post("/invoices")
            .content(InvoiceHelper.simpleInvoiceId5Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(post("/invoices")
            .content(InvoiceHelper.simpleInvoiceId6Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(post("/invoices")
            .content(InvoiceHelper.simpleInvoiceId7Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/invoices/2018-01-01/2018-01-29"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[*].identifier")
            .value(Matchers.containsInAnyOrder("TestIdentifier5", "TestIdentifier6")));
  }

  @Test
  public void shouldReturnUpdatedInvoice() throws Exception {
    mockMvc
        .perform(post(urlInvoices)
            .content(InvoiceHelper.simpleInvoiceId5Json())
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(put(urlInvoices + "/1")
            .content("{\"id\":1,\"identifier\":\"UpdatedIdentifier\",\"issuedDate\":\"2018-02-01\""
                    + ",\""
                    + "buyer\":{\"name\":\"CompanyBuyerTest5\",\"taxId\":\"5555555555\",\""
                    + "streetAndNumber\":\"Test Buyer Street 5\",\"postalCode\":\"55-555\",\""
                    + "location\":\"TestLocationBuyer4\"},\"seller\":{\"name\":\""
                    + "CompanySellerTest5\",\"taxId\":\"5555555555\",\"streetAndNumber\":\""
                    + "Test Seller Street 5\",\"postalCode\":\"55-555\",\"location\":\""
                    + "TestLocationSeller4\"},\"entries\":[]}")
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/invoices/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].identifier", is("UpdatedIdentifier")));
  }
}