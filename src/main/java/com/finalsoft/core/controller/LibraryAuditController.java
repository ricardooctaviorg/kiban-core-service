package com.finalsoft.core.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finalsoft.common.domain.bean.HealthCheck;
import com.finalsoft.common.domain.mapstruct.BookDto;
import com.finalsoft.common.domain.mapstruct.CalculoFinalDTO;
import com.finalsoft.common.domain.mapstruct.PorcentajeDTO;
import com.finalsoft.common.domain.model.FormularioBuro;
import com.finalsoft.common.domain.model.LibResponse;
import com.finalsoft.common.domain.model.RespuestaBuro;
import com.finalsoft.common.domain.model.Status;
import com.finalsoft.core.service.LibraryAuditService;

@RestController
@RequestMapping("/maxi/motor/buro")
public class LibraryAuditController {

    private static final Logger log = LoggerFactory.getLogger(LibraryAuditController.class);

    private final LibraryAuditService libraryAuditService;

    public LibraryAuditController(LibraryAuditService libraryAuditService) {
        this.libraryAuditService = libraryAuditService;
    }
    
    @GetMapping("/health")
	public HealthCheck healthCheck(){
		return new HealthCheck("core","success true");
	}

    @PostMapping("/iniciarWorkfloo")
    public ResponseEntity<RespuestaBuro> iniciarWorkfloo(@RequestBody FormularioBuro formulario) {
        return ResponseEntity.ok().body(libraryAuditService.iniciarWorkfloo( formulario));
    }

    @PostMapping("/confirmacionNip")
    public ResponseEntity<CalculoFinalDTO> confirmacionNip(@RequestBody FormularioBuro formulario) {
        return ResponseEntity.ok().body(libraryAuditService.confirmacionNip( formulario));
    }


    @PostMapping("/enviarFormulario")
    public ResponseEntity<RespuestaBuro> enviarFormulario(@RequestBody FormularioBuro formulario) {
        return ResponseEntity.ok().body(libraryAuditService.enviarFormulario( formulario));
    }

    @PostMapping("/createCuenta")
    public ResponseEntity<RespuestaBuro> createCuenta(@RequestBody FormularioBuro formulario) {
        return ResponseEntity.ok().body(libraryAuditService.createCuenta( formulario));
    }

    @PostMapping("/primeraConfirmacionNip")
    public ResponseEntity<RespuestaBuro> primeraConfirmacionNip(@RequestBody FormularioBuro formulario) {
        return ResponseEntity.ok().body(libraryAuditService.primeraConfirmacionNIP( formulario));
    }


    @PostMapping("/guardarConfig")
    public ResponseEntity<RespuestaBuro> guardarConfig(@RequestBody PorcentajeDTO porcentaje) {
        return ResponseEntity.ok().body(libraryAuditService.guardarConfig( porcentaje));
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getBooks() {
        return ResponseEntity.ok().body(libraryAuditService.getAllBooks());
    }

    @GetMapping("/requestId")
    public ResponseEntity<BookDto> getBooksWithHeaders(@RequestHeader String bookRequest) {
        return ResponseEntity.ok().body(libraryAuditService.getBooks(bookRequest));
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<LibResponse> updateBook(@PathVariable Long id, @RequestBody BookDto book) {
        return ResponseEntity.ok().body(libraryAuditService.updateBook(id, book));
    }

    @DeleteMapping(value = "{id}", produces = "application/json")
    public ResponseEntity<LibResponse> deleteBook(@PathVariable Long id) {
        LibResponse response = libraryAuditService.deleteBook(id);
        log.info("Response received : {}", response);
        log.info("Status from response : {}", Status.fetchCode(response.getResponseCode()));
        return ResponseEntity.status(Status.fetchCode(response.getResponseCode())).body(response);
    }
}

