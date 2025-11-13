/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.deskinstaller.model;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Wolverine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empresa")
public class Empresa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idempresa")
    private Integer idempresa;
    @Column(name = "razaoSocial")
    private String razaoSocial;
    @Column(name = "nomeFantasia")
    private String nomeFantasia;
    @Column(name = "tipoLogradouro")
    private String tipoLogradouro;
    @Column(name = "logradouro")
    private String logradouro;
    @Column(name = "numero")
    private String numero;
    @Column(name = "complemento")
    private String complemento;
    @Column(name = "bairro")
    private String bairro;
    @Column(name = "cidade")
    private String cidade;
    @Column(name = "cep")
    private String cep;
    @Column(name = "uf")
    private String uf;
    @Column(name = "foneComercial")
    private String foneComercial;
    @Column(name = "foneFax")
    private String foneFax;
    @Column(name = "emailnfe")
    private String emailnfe;
    @Column(name = "emailcompras")
    private String emailcompras;
    @Column(name = "cnpj")
    private String cnpj;
    @Column(name = "inscricaoEstadual")
    private String inscricaoEstadual;
     @Column(name = "idparametros")
    private int idparametros;

}
