package com.finalsoft.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalsoft.common.client.LibraryClient;
import com.finalsoft.common.domain.mapstruct.AuditDto;
import com.finalsoft.common.domain.mapstruct.BookDto;
import com.finalsoft.common.domain.mapstruct.CalculoFinalDTO;
import com.finalsoft.common.domain.mapstruct.PorcentajeDTO;
import com.finalsoft.common.domain.mapstruct.ScoreDTO;
import com.finalsoft.common.domain.model.FormularioBuro;
import com.finalsoft.common.domain.model.LibResponse;
import com.finalsoft.common.domain.model.RespuestaBuro;
import com.finalsoft.common.domain.model.RespuestaBuroScore;
import com.finalsoft.common.domain.persistence.AuditLog;
import com.finalsoft.common.domain.persistence.Porcentaje;
import com.finalsoft.common.domain.persistence.Score;
import com.finalsoft.common.mapper.AuditMapper;
import com.finalsoft.common.mapper.LibraryMapper;
import com.finalsoft.common.util.Constants;
import com.finalsoft.core.repository.AuditRepository;
import com.finalsoft.core.repository.PersonaRepository;
import com.finalsoft.core.repository.PorcentajeRepository;
import com.finalsoft.core.repository.ScoreRepository;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

@Service
public class LibraryAuditService {

    private static final Logger log = LoggerFactory.getLogger(LibraryAuditService.class);

    private final LibraryMapper libraryMapper;

    private final AuditMapper auditMapper;

    private final AuditRepository auditRepository;

    private final LibraryClient libraryClient;

    private final PersonaRepository personaRepository;

    private final PorcentajeRepository porcentajeRepository;

    private final ScoreRepository scoreRepository;

    public LibraryAuditService(LibraryMapper libraryMapper,
                               AuditMapper auditMapper, AuditRepository auditRepository, LibraryClient libraryClient, PersonaRepository personaRepository, PorcentajeRepository porcentajeRepository, ScoreRepository scoreRepository) {
        this.libraryMapper = libraryMapper;
        this.auditMapper = auditMapper;
        this.auditRepository = auditRepository;
        this.libraryClient = libraryClient;
        this.personaRepository = personaRepository;
        this.porcentajeRepository = porcentajeRepository;
        this.scoreRepository = scoreRepository;
    }

    public RespuestaBuro iniciarWorkfloo(FormularioBuro formulario) {
        log.info("Book DTO from POST request : {}", formulario);
        RespuestaBuro resp = null;
        AuditDto audit = null;
        try {

            formulario.setLogin("pablo.solis@maxikash.mx");
            formulario.setWorkflowName("Prospeccion Motos");

            Call<RespuestaBuro> callLibResponse = libraryClient.iniciarWorkfloo(formulario);
            Response<RespuestaBuro> libResponse = callLibResponse.execute();

            formulario.setFormName(libResponse.body().getData().getNextComponentName());
            formulario.setIdUnykoo(libResponse.body().getData().getIdUnykoo());


            RespuestaBuro respuestaBuro =enviarFormulario(formulario);

            respuestaBuro=createCuenta(formulario);


            if (libResponse.isSuccessful()) {
                resp = libResponse.body();
            } else {
                log.error("Error calling library client: {}", libResponse.errorBody());
                if (Objects.nonNull(libResponse.errorBody())) {

                }
            }
            if (Objects.nonNull(audit)) {
                AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
                log.info("Saved into audit successfully: {}", savedObj);
            }
            return resp;
        } catch (Exception ex) {
            log.error("Error handling retrofit call for createNewBook", ex);
            return null;//new LibResponse(Constants.ERROR, "Failed");
        }

    }

    public RespuestaBuro enviarFormulario(FormularioBuro formulario) {
        log.info("DTO from POST request : {}", formulario);
        RespuestaBuro resp = null;
        AuditDto audit = null;
        try {

            Call<RespuestaBuro> formularioResponse = libraryClient.enviarFormulario(formulario);
            Response<RespuestaBuro> libResponse = formularioResponse.execute();

            personaRepository.save(libraryMapper.PersonaDtoToPersona(formulario.getData()));


            if (libResponse.isSuccessful()) {
                resp = libResponse.body();
                //audit = auditMapper.populateAuditLogForPostAndPut(bookDto, resp, HttpMethod.POST);
            } else {
                log.error("Error calling library client: {}", libResponse.errorBody());
                if (Objects.nonNull(libResponse.errorBody())) {
                    /*audit = auditMapper.populateAuditLogForException(
                            new ObjectMapper().writeValueAsString(bookDto),
                            HttpMethod.POST, libResponse.errorBody().string());*/
                }
            }
            if (Objects.nonNull(audit)) {
                AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
                log.info("Saved into audit successfully: {}", savedObj);
            }
            return resp;
        } catch (Exception ex) {
            log.error("Error handling retrofit call for createNewBook", ex);
            return null;//new LibResponse(Constants.ERROR, "Failed");
        }

    }

    public RespuestaBuro createCuenta(FormularioBuro formulario) {
        log.info("Book DTO from POST request : {}", formulario);
        RespuestaBuro resp = null;
        AuditDto audit = null;
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n    \"login\": \"pablo.solis@maxikash.mx\",\n    \"idUnykoo\": "+formulario.getIdUnykoo()+" }");
            Request request = new Request.Builder()
                    .url("https://test.unykoo.com/workfloo/api/v2/nip/createCuenta")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("company_code", "neYDCMX")
                    .addHeader("api_key", "d5c45c9e-2ee1-4e05-9efb-21f09ae0bd0c")
                    .build();
            okhttp3.Response response = client.newCall(request).execute();

            return resp;
        } catch (Exception ex) {
            log.error("Error handling retrofit call for createNewBook", ex);
            return null;//new LibResponse(Constants.ERROR, "Failed");
        }

    }


    public CalculoFinalDTO confirmacionNip(FormularioBuro formulario) {
        log.info("DTO from POST request : {}", formulario);
        CalculoFinalDTO resp = null;
        AuditDto audit = null;

        Double  engancheDeclarado = null, engancheEstimado = null, montoFinanciar = null, engancheFinal = null, ingresoDeclarado = null;
        Double ingresoFinal = null, ingresoBuro = null, capacidadPago = null, tasaInteresAnual = null, porcentajeBCScore = null;
        Double interes52 = null, interes104 = null, interes130 = null, interes156 = null;
        Double total52 = null, total104 = null, total130 = null, total156 = null;
        Double pago52 = null, pago104 = null, pago130 = null, pago156 = null;


        RespuestaBuro respuestaBuro=primeraConfirmacionNIP(formulario);

        respuestaBuro=segundaConfirmacionNIP(formulario);

        RespuestaBuroScore respuestaBuroScore = obtenerScoreBuro(formulario.getIdUnykoo());


        LinkedHashMap<String, LinkedHashMap> consultaBuro = (LinkedHashMap<String, LinkedHashMap> ) respuestaBuroScore.getSteps().get(5);
        LinkedHashMap<String, LinkedHashMap> consultaSIC = (LinkedHashMap<String, LinkedHashMap> )consultaBuro.get("consultaSIC");
        LinkedHashMap<String, ArrayList<LinkedHashMap>> respuestaBC = (LinkedHashMap<String, ArrayList<LinkedHashMap>> )consultaSIC.get("respuestaBC");
        ArrayList<LinkedHashMap> scoreBuroCredito = (ArrayList<LinkedHashMap>) respuestaBC.get("scoreBuroCredito");
        LinkedHashMap valorScore = scoreBuroCredito.get(0);
        Integer resultadoScore= (Integer)valorScore.get("valorScore");

        List<Score> listaScore = scoreRepository.findAll(); // new ArrayList<Score>();


        tasaInteresAnual = 0.5416;

        if (engancheDeclarado > engancheEstimado)
            engancheFinal = engancheDeclarado;
        else
            engancheFinal = engancheEstimado;

        if (ingresoDeclarado < ingresoBuro)
            ingresoFinal = ingresoDeclarado;
        else
            ingresoFinal = ingresoBuro;

        capacidadPago = porcentajeBCScore * ingresoFinal;

        montoFinanciar = formulario.getPrecio_moto() - engancheFinal;

        interes52 = montoFinanciar * tasaInteresAnual;
        interes104 = montoFinanciar * tasaInteresAnual * 2;
        interes130 = montoFinanciar * tasaInteresAnual * 2.5;
        interes156 = montoFinanciar * tasaInteresAnual * 3;

        total52 = interes52 + montoFinanciar;
        total104 = interes104 + montoFinanciar;
        total130 = interes130 + montoFinanciar;
        total156 = interes156 + montoFinanciar;

        pago52 = total52 / 52;
        pago104 = total104 / 104;
        pago130 = total130 / 130;
        pago156 = total156 / 156;

        capacidadPago = (ingresoFinal * 0.35) / 4.33;

        resp.setPago52(pago52);
        resp.setPago130(pago130);
        resp.setPago104(pago104);
        resp.setPago156(pago156);
        resp.setCapacidadCliente(capacidadPago);

        return resp;
    }



    public RespuestaBuro primeraConfirmacionNIP(FormularioBuro formulario) {
        log.info("Book DTO from POST request : {}", formulario);
        RespuestaBuro resp = null;
        AuditDto audit = null;
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n    \"login\": \"pablo.solis@maxikash.mx\",\n    \"idUnykoo\": "+formulario.getIdUnykoo()+",\n  " +
                    "  \"nip\": \""+formulario.getNip()+"\"\n}");
            Request request = new Request.Builder()
                    .url("https://test.unykoo.com/workfloo/api/v2/nip/firstConfirm")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("company_code", "neYDCMX")
                    .addHeader("api_key", "d5c45c9e-2ee1-4e05-9efb-21f09ae0bd0c")
                    .build();
            okhttp3.Response response = client.newCall(request).execute();


            return resp;
        } catch (Exception ex) {
            log.error("Error handling retrofit call for createNewBook", ex);
            return null;//new LibResponse(Constants.ERROR, "Failed");
        }

    }

    public RespuestaBuro segundaConfirmacionNIP(FormularioBuro formulario) {
        log.info("Book DTO from POST request : {}", formulario);
        RespuestaBuro resp = null;
        AuditDto audit = null;
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n    \"login\": \"pablo.solis@maxikash.mx\",\n    \"idUnykoo\": "+formulario.getIdUnykoo()+",\n  " +
                    "  \"nip\": \""+formulario.getNip()+"\"\n}");
            Request request = new Request.Builder()
                    .url("https://test.unykoo.com/workfloo/api/v2/nip/secondConfirm")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("company_code", "neYDCMX")
                    .addHeader("api_key", "d5c45c9e-2ee1-4e05-9efb-21f09ae0bd0c")
                    .build();
            okhttp3.Response response = client.newCall(request).execute();

            return resp;
        } catch (Exception ex) {
            log.error("Error handling retrofit call for createNewBook", ex);
            return null;//new LibResponse(Constants.ERROR, "Failed");
        }

    }



    public RespuestaBuroScore obtenerScoreBuro(String idUnykoo) {
        log.info("Buro DTO from GET request : {}", idUnykoo);
        RespuestaBuro resp = null;
        AuditDto audit = null;
        try {


            Call<RespuestaBuroScore> formularioResponse = libraryClient.obtenerScoreBuro(idUnykoo);
            Response<RespuestaBuroScore> libResponse = formularioResponse.execute();


            return libResponse.body();
        } catch (Exception ex) {
            log.error("Error handling retrofit call for createNewBook", ex);
            return null;//new LibResponse(Constants.ERROR, "Failed");
        }

    }

    public RespuestaBuro guardarConfig(PorcentajeDTO porcentaje) {
        log.info("Book DTO from POST request : {}", porcentaje);
        RespuestaBuro resp = null;

        Porcentaje porcentajeEntity= libraryMapper.PorcentajeDtoToPorcentaje(porcentaje);

         porcentajeRepository.save(porcentajeEntity);

        for(ScoreDTO score: porcentaje.getScores()){

            scoreRepository.save(libraryMapper.ScoreDtoToScore(score,porcentajeEntity));

        }



        return resp;
    }






    /*public void getBooksAsync(String bookRequest) {
        Call<BookDto> bookDtoCall = libraryClient.getAllBooksWithHeaders(bookRequest);
        bookDtoCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<BookDto> call, Response<BookDto> response) {
                if (response.isSuccessful()) {
                    log.info("Success response : {}", response.body());
                } else {
                    log.info("Error response : {}", response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<BookDto> call, Throwable throwable) {
                log.info("Network error occured : {}", throwable.getLocalizedMessage());
            }
        });
    }*/

    public BookDto getBooks(String bookRequest) {
        Response<BookDto> allBooksResponse = null;
        BookDto books = null;
        try {
            AuditDto audit = null;
            allBooksResponse = libraryClient.getAllBooksWithHeaders(bookRequest).execute();
            if (allBooksResponse.isSuccessful()) {
                books = allBooksResponse.body();
                log.info("Get All Books : {}", books);
                audit = auditMapper.populateAuditLogForGetBook(books);
            } else {
                log.error("Error calling library client: {}", allBooksResponse.errorBody());
                if (Objects.nonNull(allBooksResponse.errorBody())) {
                    audit = auditMapper.populateAuditLogForException(
                            null, HttpMethod.GET, allBooksResponse.errorBody().string());
                }

            }

            if (Objects.nonNull(audit)) {
                AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
                log.info("Saved into audit successfully: {}", savedObj);
            }
            return books;
        } catch (Exception ex) {
            log.error("Error handling retrofit call for getAllBooks", ex);
            return books;
        }

    }

    public List<BookDto> getAllBooks() {
        List<BookDto> books = Collections.emptyList();
        try {
            AuditDto audit = null;
            Call<List<BookDto>> callBookResponse = libraryClient.getAllBooks("all");
            Response<List<BookDto>> allBooksResponse = callBookResponse.execute();
            if (allBooksResponse.isSuccessful()) {
                books = allBooksResponse.body();
                log.info("Get All Books : {}", books);
                audit = auditMapper.populateAuditLogForGet(books);
            } else {
                log.error("Error calling library client: {}", allBooksResponse.errorBody());
                if (Objects.nonNull(allBooksResponse.errorBody())) {
                    audit = auditMapper.populateAuditLogForException(
                            null, HttpMethod.GET, allBooksResponse.errorBody().string());
                }

            }

            if (Objects.nonNull(audit)) {
                AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
                log.info("Saved into audit successfully: {}", savedObj);
            }
            return books;
        } catch (Exception ex) {
            log.error("Error handling retrofit call for getAllBooks", ex);
            return books;
        }

    }

    public LibResponse updateBook(Long id, BookDto bookdto) {
        log.info("Book DTO from PUT request : {}", bookdto);
        LibResponse resp = null;
        AuditDto audit = null;
        try {
            Call<LibResponse> callLibResponse = libraryClient.updateBook(id, bookdto);
            Response<LibResponse> libResponse = callLibResponse.execute();
            if (libResponse.isSuccessful()) {
                resp = libResponse.body();
                audit = auditMapper.populateAuditLogForPostAndPut(bookdto, resp, HttpMethod.PUT);
            } else {
                log.error("Error calling library client: {}", libResponse.errorBody());
                if (Objects.nonNull(libResponse.errorBody())) {
                    audit = auditMapper.populateAuditLogForException(
                            new ObjectMapper().writeValueAsString(bookdto),
                            HttpMethod.POST, libResponse.errorBody().string());
                }
            }
        } catch (Exception ex) {
            log.error("Error handling retrofit call for updateBook", ex);
            resp = new LibResponse(Constants.ERROR, "Failed");
        }

        if (Objects.nonNull(audit)) {
            AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
            log.info("Saved into audit successfully: {}", savedObj);
        }
        return resp;
    }

    public LibResponse deleteBook(Long id) {
        LibResponse resp = null;
        AuditDto audit = null;
        try {
            Call<LibResponse> callLibResponse = libraryClient.deleteBook(id);
            Response<LibResponse> libResponse = callLibResponse.execute();

            if (libResponse.isSuccessful()) {
                resp = libResponse.body();
                audit = auditMapper.populateAuditLogForDelete(id, resp);
            } else {
                log.error("Error calling library client: {}", libResponse.errorBody());
                if (Objects.nonNull(libResponse.errorBody())) {
                    resp = new ObjectMapper().readValue(libResponse.errorBody().string(), LibResponse.class);
                    audit = auditMapper.populateAuditLogForException(
                            String.valueOf(id), HttpMethod.POST, libResponse.errorBody().string());
                }
            }
        } catch (Exception ex) {
            log.error("Error handling retrofit call for deleteBook", ex);
            resp = new LibResponse(Constants.ERROR, "Failed");
        }


        if (Objects.nonNull(audit)) {
            AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
            log.info("Saved into audit successfully : {}", savedObj);
        }
        return resp;
    }
}
