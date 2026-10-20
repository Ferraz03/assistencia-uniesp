package br.edu.uniesp.assistencia.internal.ordemservico.controller;

import br.edu.uniesp.assistencia.internal.ordemservico.dto.CriarOrdemServicoRequest;
import br.edu.uniesp.assistencia.internal.ordemservico.dto.CriarOrdemServicoResponse;
import br.edu.uniesp.assistencia.internal.ordemservico.service.OrdemServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordens")
public class OrdemServicoController {

    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CriarOrdemServicoResponse> criar(
            @RequestBody @Valid CriarOrdemServicoRequest request) {
        CriarOrdemServicoResponse response = service.criarOrdemServico(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}