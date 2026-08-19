package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.OsFuncionarioDTO;
import jakarta.validation.Valid;
import br.com.deskinstaller.service.OsFuncionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/os/funcionario")
@RequiredArgsConstructor
public class OsFuncionarioController {

    private final OsFuncionarioService osFuncionarioService;


    @GetMapping("/{id}")
    public List<OsFuncionarioDTO> buscarPorOrdemServico(@PathVariable Integer id) {
        return osFuncionarioService.buscarPorIdOrdemServico(id);
    }

    @PostMapping
    public ResponseEntity<OsFuncionarioDTO> salvar(@Valid @RequestBody OsFuncionarioDTO dto) {
        OsFuncionarioDTO salvo = osFuncionarioService.salvar(dto);
        return ResponseEntity.status(201).body(salvo);
    }

    @PostMapping("/salvar")
    public ResponseEntity<OsFuncionarioDTO> salvarLegado(@Valid @RequestBody OsFuncionarioDTO dto) {
        return salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OsFuncionarioDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody OsFuncionarioDTO dto) {
        dto.setIdosFuncionario(id);
        return ResponseEntity.ok(osFuncionarioService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        osFuncionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarLegado(@PathVariable Integer id) {
        return deletar(id);
    }
}
