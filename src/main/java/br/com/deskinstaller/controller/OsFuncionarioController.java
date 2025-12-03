package br.com.deskinstaller.controller;

import br.com.deskinstaller.dto.OsFuncionarioDTO;
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

    @PostMapping("/salvar"    )
    public ResponseEntity<OsFuncionarioDTO> salvar(@RequestBody OsFuncionarioDTO dto) {
        OsFuncionarioDTO salvo = osFuncionarioService.salvar(dto);
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        osFuncionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

