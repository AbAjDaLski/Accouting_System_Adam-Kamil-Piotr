package pl.coderstrust.accounting.logic;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.coderstrust.accounting.database.Database;
import pl.coderstrust.accounting.helpers.InvoiceHelper;
import pl.coderstrust.accounting.model.Invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@RunWith(JUnitParamsRunner.class)
public class TaxCalculatorServiceTest {

  @Mock
  private Database databaseMock;

  @InjectMocks
  private TaxCalculatorService taxCalculatorService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  List<List<Invoice>> parameters = Arrays.asList(
      Arrays.asList(InvoiceHelper.getSampleInvoiceWithId0(),
          InvoiceHelper.getSampleInvoiceWithId1(),InvoiceHelper.getSampleInvoiceWithId2(),
          InvoiceHelper.getSampleInvoiceWithId3(), InvoiceHelper.getSampleInvoiceWithId4())

  );

  @Parameters(method = "ParametersForShouldCalculateRevenuesWhenInvoicesBooked")
  @Test
  public void shouldCalculateRevenuesWhenInvoicesBooked(List sampleDatabase) {

    //given
    BigDecimal expected = BigDecimal.valueOf(50);


    //when
    when(databaseMock.getAll()).thenReturn(sampleDatabase);
    BigDecimal actual = taxCalculatorService.getRevenues();

    //then
    assertEquals(expected, actual);
  }

  private List<List<Invoice>> ParametersForShouldCalculateRevenuesWhenInvoicesBooked() {

    return parameters;
  }

  @Parameters(method = "ParametersForShouldCalculateCostsWhenInvoicesBooked")
  @Test
  public void shouldCalculateCostsWhenInvoicesBooked(List sampleDatabase) {

    //given
    BigDecimal expected = BigDecimal.valueOf(60);

    //when
    when(databaseMock.getAll()).thenReturn(sampleDatabase);
    BigDecimal actual = taxCalculatorService.getCosts();

    //then
    assertEquals(expected, actual);
  }

  private List<List<Invoice>> ParametersForShouldCalculateCostsWhenInvoicesBooked() {

    return parameters;
  }

  @Parameters(method = "ParametersForShouldCalculateVatReceivableWhenInvoicesBooked")
  @Test
  public void shouldCalculateVatReceivableWhenInvoicesBooked(List sampleDatabase) {

    //given
    BigDecimal expected = BigDecimal.valueOf(5.2);

    //when
    when(databaseMock.getAll()).thenReturn(sampleDatabase);
    BigDecimal actual = taxCalculatorService.getVatReceivable();

    //then
    assertEquals(expected.setScale(2, RoundingMode.CEILING), actual);

  }

  private List<List<Invoice>> ParametersForShouldCalculateVatReceivableWhenInvoicesBooked() {

    return parameters;
  }

  @Parameters(method = "ParametersForShouldCalculateVatPayableWhenInvoicesBooked")
  @Test
  public void shouldCalculateVatPayableWhenInvoicesBooked(List sampleDatabase) {

    //given
    BigDecimal expected = BigDecimal.valueOf(4.4);


    //when
    when(databaseMock.getAll()).thenReturn(sampleDatabase);
    BigDecimal actual = taxCalculatorService.getVatPayable();

    //then
    assertEquals(expected.setScale(2, RoundingMode.CEILING), actual);
  }

  private List<List<Invoice>> ParametersForShouldCalculateVatPayableWhenInvoicesBooked() {

    return parameters;
  }
}