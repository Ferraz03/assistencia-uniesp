package br.edu.uniesp.assistencia.internal.ordemservico.service;

import br.edu.uniesp.assistencia.internal.cliente.entity.ClienteEntity;
import br.edu.uniesp.assistencia.internal.cliente.repository.ClienteRepository;
import br.edu.uniesp.assistencia.internal.equipamento.entity.EquipamentoEntity;
import br.edu.uniesp.assistencia.internal.equipamento.repository.EquipamentoRepository;
import br.edu.uniesp.assistencia.internal.ordemservico.dto.CriarOrdemServicoRequest;
import br.edu.uniesp.assistencia.internal.ordemservico.dto.CriarOrdemServicoResponse;
import br.edu.uniesp.assistencia.internal.ordemservico.entity.OrdemServicoEntity;
import br.edu.uniesp.assistencia.internal.ordemservico.repository.OrdemServicoRepository;
import br.edu.uniesp.assistencia.shared.exception.RecursoNaoEncontradoException;
import br.edu.uniesp.assistencia.shared.exception.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final EquipamentoRepository equipamentoRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               ClienteRepository clienteRepository,
                               EquipamentoRepository equipamentoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    @Transactional
    public CriarOrdemServicoResponse criarOrdemServico(CriarOrdemServicoRequest request) {
        // Cenário 3: Busca do Cliente (Lança HTTP 404 se não achar)
        ClienteEntity cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", request.clienteId()));

        // Cenário 3: Busca do Equipamento (Lança HTTP 404 se não achar)
        EquipamentoEntity equipamento = equipamentoRepository.findById(request.equipamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipamento", request.equipamentoId()));

        // RN02: Validação de Propriedade do Equipamento (Cenário 5 - HTTP 422)
        if (!equipamento.getCliente().getId().equals(cliente.getId())) {
            throw new RegraNegocioException("O equipamento informado não pertence ao cliente indicado.");
        }

        LocalDateTime dataAtual = LocalDateTime.now();
        String protocolo = gerarProtocoloUnico(dataAtual);

        // Utiliza o construtor rico da Entidade (que já valida cliente ativo RN01,
        // define status RECEBIDA RN03, data de abertura e histórico RN14)
        OrdemServicoEntity ordemServico = new OrdemServicoEntity(
                protocolo,
                cliente,
                equipamento,
                request.descricaoProblema()
        );

        OrdemServicoEntity osSalva = ordemServicoRepository.save(ordemServico);

        // Retorna o DTO de Resposta com os dados gerados
        return new CriarOrdemServicoResponse(
                osSalva.getId(),
                osSalva.getNumeroProtocolo(),
                osSalva.getStatus(),
                osSalva.getDataAbertura()
        );
    }

    private String gerarProtocoloUnico(LocalDateTime data) {
        String dataStr = data.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String hash = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "OS-" + dataStr + "-" + hash;
    }
}