package pl.coderstrust.accounting.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.accounting.logic.InvoiceService;
import pl.coderstrust.accounting.logic.pdf.PdfService;
import pl.coderstrust.accounting.logic.pdf.PdfSet;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.validator.InvoiceValidator;
import pl.coderstrust.accounting.model.validator.exception.InvoiceValidationException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/invoices")
@Api(value = "/{invoices}", description = "Operations on invoices")
public class InvoiceController {

  private static Logger logger = LoggerFactory.getLogger(InvoiceController.class);

  private InvoiceService invoiceService;
  private InvoiceValidator invoiceValidator;
  private PdfService invoicePdf;

  public InvoiceController(InvoiceService invoiceService, InvoiceValidator invoiceValidator, PdfService invoicePdf) {
    this.invoiceService = invoiceService;
    this.invoiceValidator = invoiceValidator;
    this.invoicePdf = invoicePdf;
  }

  @ApiOperation(value = "Find all invoices",
      notes = "Method returns list of all invoices")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "All invoices"),
      @ApiResponse(code = 401, message = "Access unauthorized "),
      @ApiResponse(code = 403, message = "Access forbidden "),
      @ApiResponse(code = 404, message = "List of invoices is empty")})
  @GetMapping
  public Collection<Invoice> findInvoices() {
    logger.info("Received find invoices request");
    return invoiceService.getAll();
  }

  @ApiOperation(value = "Find invoices by id",
      notes = "Method returns invoice with in demand id")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Found invoice"),
      @ApiResponse(code = 403, message = "Access forbidden "),
      @ApiResponse(code = 401, message = "Access unauthorized "),
      @ApiResponse(code = 400, message = "Bad format date, use number"),
      @ApiResponse(code = 404, message = "Invoice is not exist")})
  @GetMapping("/{id}")
  public ResponseEntity<?> findInvoiceById(
      @PathVariable(name = "id", required = true) int id) {
    Optional<Invoice> invoiceOptional = invoiceService.findById(id);
    return invoiceOptional.isPresent()
        ? ResponseEntity.ok().body(invoiceOptional.get())
        : ResponseEntity.notFound().build();
  }

  @ApiOperation(value = "Find invoices from the date range",
      notes = "Method returns all invoices from the date range")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Found invoices"),
      @ApiResponse(code = 400, message = "Bad format date, insert bad format, use format YYYY-MM-DD"),
      @ApiResponse(code = 401, message = "Access unauthorized "),
      @ApiResponse(code = 403, message = "Access forbidden "),
      @ApiResponse(code = 404, message = "Invoices is not exist")})
  @GetMapping("{dateFrom}/{dateTo}")
  public Collection<Invoice> findSingleInvoice(
      @PathVariable("dateFrom") @DateTimeFormat(iso = ISO.DATE) LocalDate dateFrom,
      @PathVariable("dateTo") @DateTimeFormat(iso = ISO.DATE) LocalDate dateTo) {
    logger.info("Received find invoices withing set date range request");
    return invoiceService
        .findInvoices(null,
            dateFrom, dateTo);
  }

  @ApiOperation(value = "Find invoices by parameter",
      notes = "Method returns invoices according to search parameter")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Found invoice"),
      @ApiResponse(code = 400, message = "Bad format date, insert bad format, use format YYYY-MM-DD"),
      @ApiResponse(code = 401, message = "Access unauthorized "),
      @ApiResponse(code = 403, message = "Access forbidden "),
      @ApiResponse(code = 404, message = "Invoices is not exist")})
  @PostMapping("/searchParams")
  public Collection<Invoice> findeSingleInvoiceBysearchparams(@RequestBody Invoice searchParams) {
    return invoiceService.findInvoices(searchParams, null, null);
  }

  @ApiOperation(value = "Add invoice",
      notes = "Method add new invoice")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Added new invoice"),
      @ApiResponse(code = 400, message = "Bad format invoice, use format json"),
      @ApiResponse(code = 401, message = "Access unauthorized "),
      @ApiResponse(code = 403, message = "Access forbidden ")})
  @PostMapping
  public ResponseEntity<?> saveInvoice(@RequestBody Invoice invoice) {
    logger.info("Received save invoice request");
    Collection<InvoiceValidationException> validationErrors = invoiceValidator
        .validateInvoiceForSave(invoice);
    if (validationErrors.isEmpty()) {
      return ResponseEntity.ok(invoiceService.saveInvoice(invoice));
    }
    return ResponseEntity.badRequest().body(validationErrors);
  }

  @ApiOperation(value = "Remove invoice by id",
      notes = "Method remove exist invoice")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Removed invoice"),
      @ApiResponse(code = 400, message = "Bad format date, use number"),
      @ApiResponse(code = 401, message = "Access unauthorized "),
      @ApiResponse(code = 403, message = "Access forbidden "),
      @ApiResponse(code = 500, message = "Didn't remove, invoice is not exist")})
  @DeleteMapping
  public void removeInvoice(@RequestBody int id) {
    logger.info("Received remove invoice request");
    invoiceService.removeInvoice(id);
  }

  @ApiOperation(value = "Update invoice by id",
      notes = "Method update exist invoice")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Updated invoice"),
      @ApiResponse(code = 401, message = "Access unauthorized "),
      @ApiResponse(code = 403, message = "Access forbidden "),
      @ApiResponse(code = 400, message = "insert bad format, use format YYYY-MM-DD"),
      @ApiResponse(code = 500, message = "Didn't update, invoice is not exist")})
  @PutMapping("/{id}")
  public ResponseEntity<?> updateInvoice(@PathVariable int id, @RequestBody Invoice invoice) {
    logger.info("Received update invoice request");
    Collection<InvoiceValidationException> validationErrors = invoiceValidator
        .validateInvoiceForUpdate(invoice);
    if (!validationErrors.isEmpty()) {
      return ResponseEntity.badRequest().body(validationErrors);
    }
    invoiceService.updateInvoice(invoice);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "pdf/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
  @ApiOperation(value = "Returns invoice in pdf format")
  public ResponseEntity<InputStreamResource> invoicePdf(@PathVariable("id") int id) {
    Optional<Invoice> invoiceFromDatabase = invoiceService.findById(id);
    if (!invoiceFromDatabase.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    try {
      new PdfService().generatePdf(PdfSet.createContent(invoiceFromDatabase.get()));
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
    return ResponseEntity.ok().build();
  }
}
