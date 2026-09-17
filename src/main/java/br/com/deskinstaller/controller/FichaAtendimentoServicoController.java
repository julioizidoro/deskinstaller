package br.com.deskinstaller.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.deskinstaller.dto.FichaAtendimentoServicoDTO;
import br.com.deskinstaller.exception.ResourceNotFoundException;
import br.com.deskinstaller.service.FichaAtendimentoServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoints dos itens de servico da ficha de atendimento.
 *
 * @author Julio Izidoro
 */
@RestController
@RequestMapping({"/api/fichas-atendimento-servicos", "/api/fichaatendimentoservico"})
@RequiredArgsConstructor
@Slf4j
public class FichaAtendimentoServicoController {

    private final FichaAtendimentoServicoService fichaAtendimentoServicoService;

    @GetMapping("/ficha/{id}")
    public ResponseEntity<List<FichaAtendimentoServicoDTO>> listarPorFicha(@PathVariable("id") Integer idficha) {
        log.info("GET /api/fichas-atendimento-servicos/ficha/{}", idficha);
        return ResponseEntity.ok(fichaAtendimentoServicoService.listarPorFicha(idficha));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaAtendimentoServicoDTO> buscarPorId(@PathVariable Integer id) {
        return fichaAtendimentoServicoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Item de serviço não encontrado com ID: " + id));
    }

    @PostMapping
    public ResponseEntity<FichaAtendimentoServicoDTO> salvar(@Valid @RequestBody FichaAtendimentoServicoDTO dto) {
        log.info("POST /api/fichas-atendimento-servicos - salvar item");
        return ResponseEntity.status(HttpStatus.CREATED).body(fichaAtendimentoServicoService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FichaAtendimentoServicoDTO> atualizar(@PathVariable Integer id,
                                                                 @Valid @RequestBody FichaAtendimentoServicoDTO dto) {
        dto.setIdfichaatendimentosdrvico(id);
        return ResponseEntity.ok(fichaAtendimentoServicoService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        fichaAtendimentoServicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
